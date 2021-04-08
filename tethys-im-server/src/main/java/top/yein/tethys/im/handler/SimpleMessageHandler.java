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

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.constants.MessageContentType;
import top.yein.tethys.constants.MessageKind;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.MessageProperties;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.message.MessageRouter;
import top.yein.tethys.packet.SimpleMessagePacket;
import top.yein.tethys.service.MessageStorageService;
import top.yein.tethys.session.Session;
import top.yein.tethys.util.YeinGid;

/**
 * {@link SimpleMessagePacket} 处理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class SimpleMessageHandler implements PacketHandler<SimpleMessagePacket> {

  private final MessageProperties messageProperties;
  private final MessageIdGenerator messageIdGenerator;
  private final MessageRouter messageRouter;
  private final MessageStorageService messageStorageService;

  /**
   * 可被 IoC 容器所使用的构造函数.
   *
   * @param messageProperties 消息配置
   * @param messageIdGenerator 消息 ID 生成器
   * @param messageRouter 消息路由器
   * @param messageStorageService 消息存储服务
   */
  @Inject
  public SimpleMessageHandler(
      MessageProperties messageProperties,
      MessageIdGenerator messageIdGenerator,
      MessageRouter messageRouter,
      MessageStorageService messageStorageService) {
    this.messageProperties = messageProperties;
    this.messageIdGenerator = messageIdGenerator;
    this.messageRouter = messageRouter;
    this.messageStorageService = messageStorageService;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull SimpleMessagePacket packet) {
    var kind = MessageKind.forCode(packet.getKind());
    if (kind != MessageKind.P_MESSAGE && kind != MessageKind.G_MESSAGE) {
      var m =
          Strings.lenientFormat(
              "[kind]可选值范围[%s,%s], 不支持[kind=%s]",
              MessageKind.P_MESSAGE.getCode(), MessageKind.G_MESSAGE.getCode(), packet.getKind());
      throw new BizCodeException(BizCodes.C3522, m);
    }

    if (packet.getMessageId() == null && messageProperties.isAutofillId()) {
      // 自动填充消息 ID
      packet.setMessageId(messageIdGenerator.nextId());
      log.debug("自动填充私聊消息 ID, packet={}, session={}", packet, session);
    }
    // 校验 message_id
    if (packet.getMessageId().length() != YeinGid.YEIN_GID_LENGTH) {
      throw new BizCodeException(BizCodes.C3600, "[message_id]不能为空且必须是一个长度为 15 的字符串");
    }
    if (CharMatcher.whitespace().matchesAnyOf(packet.getMessageId())) {
      throw new BizCodeException(BizCodes.C3600, "[message_id]不能包含空白字符")
          .addContextValue("message_id", packet.getMessageId());
    }

    if (packet.getTo() <= 0) {
      throw new BizCodeException(BizCodes.C3600, "[to]值不合法").addContextValue("to", packet.getTo());
    }

    // 校验私聊消息与当前会话是否匹配
    if (packet.getFrom() != null && !Objects.equals(packet.getFrom(), session.uid())) {
      throw new BizCodeException(BizCodes.C3501);
    }

    // 当 from 为空时自动填充为当前认证用户的 ID
    if (packet.getFrom() == null) {
      packet.setFrom(session.uid());
    }

    if (Strings.isNullOrEmpty(packet.getContent())) {
      throw new BizCodeException(BizCodes.C3600, "[content]值能为空");
    }

    if (MessageContentType.forCode(packet.getContentType()) == MessageContentType.UNRECOGNIZED) {
      throw new BizCodeException(BizCodes.C3600, "[content_type]值不合法")
          .addContextValue("content_type", packet.getContentType());
    }

    return Mono.when(
        // 消息存储
        messageStorageService.store(packet),
        // 路由消息
        messageRouter.route(packet));
  }
}
