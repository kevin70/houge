/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.yein.tethys.im.handler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.Nil;
import top.yein.tethys.constants.MessageReadStatus;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.MessageProperties;
import top.yein.tethys.entity.Message;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.PrivateMessagePacket;
import top.yein.tethys.service.UserService;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionManager;
import top.yein.tethys.storage.MessageDao;

/**
 * 私聊处理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class PrivateMessageHandler implements PacketHandler<PrivateMessagePacket> {

  private final SessionManager sessionManager;
  private final MessageDao messageDao;
  private final MessageProperties messageProperties;
  private final MessageIdGenerator messageIdGenerator;
  private final UserService userService;

  /**
   * 构造函数.
   *
   * @param sessionManager 会话管理器
   * @param messageDao
   * @param messageProperties 聊天消息静态配置
   * @param messageIdGenerator 消息 ID 生成器
   * @param userService
   */
  @Inject
  public PrivateMessageHandler(
      SessionManager sessionManager,
      MessageDao messageDao,
      MessageProperties messageProperties,
      MessageIdGenerator messageIdGenerator,
      UserService userService) {
    this.sessionManager = sessionManager;
    this.messageDao = messageDao;
    this.messageProperties = messageProperties;
    this.messageIdGenerator = messageIdGenerator;
    this.userService = userService;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull PrivateMessagePacket packet) {
    if (packet.getMessageId() == null && messageProperties.isAutofillId()) {
      packet.setMessageId(messageIdGenerator.nextId());
      log.debug("自动填充私聊消息 ID, packet={}, session={}", packet, session);
    }

    // 校验私聊消息与当前会话是否匹配
    if (packet.getFrom() != null && !Objects.equals(packet.getFrom(), session.uid())) {
      throw new BizCodeException(BizCodes.C3501);
    }

    // 校验消息
    // FIXME
    //    MessagePacketChecker.check(packet);
    var from = Optional.ofNullable(packet.getFrom()).orElseGet(session::uid);
    var to = packet.getTo();

    // 路由、存储消息
    Function<Nil, Mono<Void>> bizFun =
        (unused) -> {
          var p1 =
              sessionManager.findByUid(to).delayUntil(toSession -> toSession.sendPacket(packet));

          // 存储的消息实体
          var entity =
              Message.builder()
                  .id(packet.getMessageId())
                  .senderId(from)
                  .receiverId(to)
                  .kind(Message.KIND_PRIVATE)
                  .content(packet.getContent())
                  .contentKind(packet.getContentKind())
                  .url(packet.getUrl())
                  .customArgs(packet.getCustomArgs())
                  .unread(MessageReadStatus.UNREAD.getCode())
                  .build();
          var p2 = messageDao.insert(entity, List.of(from, to));
          return p2.thenMany(p1).then();
        };

    return userService
        .existsById(to)
        .switchIfEmpty(
            Mono.error(
                () -> {
                  var e = new BizCodeException(BizCodes.C3630).addContextValue("to", to);
                  log.warn("用户[{}]发送私聊消息给不存在的用户[{}] {}", from, to, packet);
                  return e;
                }))
        .flatMap(bizFun);
  }
}
