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
package cool.houge.logic.handler;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import cool.houge.logic.handler.internal.MessagePacketHelper;
import java.util.List;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import cool.houge.BizCodes;
import cool.houge.id.MessageIdGenerator;
import cool.houge.logic.agent.PacketSender;
import cool.houge.logic.packet.GroupMessagePacket;
import cool.houge.service.MessageStorageService;
import cool.houge.storage.query.GroupQueryDao;
import cool.houge.util.YeinGid;

/**
 * 群组消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupMessageHandler implements PacketHandler<GroupMessagePacket> {

  private static final Logger log = LogManager.getLogger();

  private final MessageIdGenerator messageIdGenerator;
  private final MessageStorageService messageStorageService;
  private final PacketSender packetSender;
  private final GroupQueryDao groupQueryDao;

  /**
   * @param messageIdGenerator
   * @param messageStorageService
   * @param packetSender
   * @param groupQueryDao
   */
  @Inject
  public GroupMessageHandler(
      MessageIdGenerator messageIdGenerator,
      MessageStorageService messageStorageService,
      PacketSender packetSender,
      GroupQueryDao groupQueryDao) {
    this.messageIdGenerator = messageIdGenerator;
    this.messageStorageService = messageStorageService;
    this.packetSender = packetSender;
    this.groupQueryDao = groupQueryDao;
  }

  @Override
  public Mono<Result> handle(GroupMessagePacket packet) {
    if (packet.getMessageId() == null) {
      // 自动填充消息 ID
      packet.setMessageId(messageIdGenerator.nextId());
      log.debug("自动填充群聊消息 ID, packet={}", packet);
    }
    // 校验 message_id
    if (packet.getMessageId().length() != YeinGid.YEIN_GID_LENGTH) {
      return Mono.just(Result.error(BizCodes.C3600.getCode(), "[message_id]不能为空且必须是一个长度为 15 的字符串"));
    }
    if (CharMatcher.whitespace().matchesAnyOf(packet.getMessageId())) {
      return Mono.just(Result.error(BizCodes.C3600.getCode(), "[message_id]不能包含空白字符"));
    }

    return groupQueryDao
        .existsById(packet.getTo())
        .doOnNext(uids -> packetSender.sendToGroup(List.of(packet.getTo()), packet))
        .flatMapMany(unused -> groupQueryDao.queryUidByGid(packet.getTo()))
        .collectList()
        .flatMap(
            uids -> {
              if (uids.isEmpty()) {
                return Mono.empty();
              }

              // 存储消息
              var entity = MessagePacketHelper.toMessageEntity(packet);
              return messageStorageService.store(entity, uids).thenReturn(Result.ok());
            })
        .switchIfEmpty(
            Mono.fromSupplier(
                () ->
                    Result.error(
                        BizCodes.C3630.getCode(),
                        Strings.lenientFormat("群组不存在[gid=%s]", packet.getTo()))));
  }
}
