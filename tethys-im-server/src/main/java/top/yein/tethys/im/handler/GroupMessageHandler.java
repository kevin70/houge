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

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.netty.ReactorNetty;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.constants.ReadStatus;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.MessageProperties;
import top.yein.tethys.core.util.PacketUtils;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.im.handler.internal.MessagePacketChecker;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.GroupMessagePacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.storage.MessageDao;
import top.yein.tethys.storage.entity.Message;
import top.yein.tethys.storage.query.GroupQueryDao;

/**
 * 群组消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class GroupMessageHandler implements PacketHandler<GroupMessagePacket> {

  private final MessageProperties messageProperties;
  private final MessageIdGenerator messageIdGenerator;
  private final SessionGroupManager sessionGroupManager;

  private final GroupQueryDao groupQueryDao;
  private final MessageDao messageDao;

  /**
   * 构造函数.
   *
   * @param messageProperties 聊天消息静态配置
   * @param messageIdGenerator 消息 ID 生成器
   * @param sessionGroupManager 群组会话管理对象
   * @param messageDao 消息存储数据访问对象
   * @param groupQueryDao 群组查询数据访问对象
   */
  @Inject
  public GroupMessageHandler(
      MessageProperties messageProperties,
      MessageIdGenerator messageIdGenerator,
      SessionGroupManager sessionGroupManager,
      MessageDao messageDao,
      GroupQueryDao groupQueryDao) {
    this.sessionGroupManager = sessionGroupManager;
    this.messageDao = messageDao;
    this.messageProperties = messageProperties;
    this.messageIdGenerator = messageIdGenerator;
    this.groupQueryDao = groupQueryDao;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull GroupMessagePacket packet) {
    if (packet.getMessageId() == null && messageProperties.isAutofillId()) {
      packet.setMessageId(messageIdGenerator.nextId());
      log.debug("自动填充群组消息 ID, packet={}, session={}", packet, session);
    }

    // 校验消息
    MessagePacketChecker.check(packet);

    var groupId = packet.getTo();
    var from = Optional.ofNullable(packet.getFrom()).orElseGet(session::uid);

    // ----------------------------------------------------------------------------
    // TIPS: 针对群组消息优化
    // 一次序列化并消息推送给群组所有成员
    ByteBuf byteBuf;
    try {
      byteBuf = PacketUtils.toByteBuf(packet);
    } catch (IOException e) {
      log.error("[session={}]序列化 Packet 异常 {}", session, packet, e);
      return Mono.error(new BizCodeException(BizCodes.C3601));
    }

    var sendMono =
        sessionGroupManager
            .findByGroupId(groupId)
            .filter(toSession -> toSession != session)
            .flatMap(toSession -> toSession.send(Mono.fromSupplier(byteBuf::retainedDuplicate)))
            // TIPS: 手动释放 Netty ByteBuf
            .doOnCancel(() -> ReactorNetty.safeRelease(byteBuf))
            .doOnTerminate(() -> ReactorNetty.safeRelease(byteBuf));

    // 存储的消息实体
    var entity =
        Message.builder()
            .id(packet.getMessageId())
            .senderId(from)
            .groupId(groupId)
            .kind(Message.KIND_GROUP)
            .content(packet.getContent())
            .contentKind(packet.getContentKind())
            .url(packet.getUrl())
            .customArgs(packet.getCustomArgs())
            .unread(ReadStatus.UNREAD.getCode())
            .build();
    var storageMono =
        groupQueryDao
            .queryUidByGid(groupId)
            .collectList()
            .flatMap(memberIds -> messageDao.insert(entity, memberIds));
    return storageMono.thenMany(sendMono).then();
  }
}
