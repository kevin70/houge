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
package top.yein.tethys.logic.handler;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import java.util.List;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import top.yein.tethys.BizCodes;
import top.yein.tethys.constants.MessageContentType;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.logic.agent.PacketSender;
import top.yein.tethys.logic.handler.internal.MessagePacketHelper;
import top.yein.tethys.logic.packet.PrivateMessagePacket;
import top.yein.tethys.service.MessageStorageService;
import top.yein.tethys.util.YeinGid;

/**
 * 私人消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class PrivateMessageHandler implements PacketHandler<PrivateMessagePacket> {

  private static final Logger log = LogManager.getLogger();

  private final MessageIdGenerator messageIdGenerator;
  private final MessageStorageService messageStorageService;
  private final PacketSender packetSender;

  /**
   * @param messageIdGenerator
   * @param messageStorageService
   * @param packetSender
   */
  @Inject
  public PrivateMessageHandler(
      MessageIdGenerator messageIdGenerator,
      MessageStorageService messageStorageService,
      PacketSender packetSender) {
    this.messageIdGenerator = messageIdGenerator;
    this.messageStorageService = messageStorageService;
    this.packetSender = packetSender;
  }

  @Override
  public Mono<Result> handle(PrivateMessagePacket packet) {
    if (packet.getMessageId() == null) {
      // 自动填充消息 ID
      packet.setMessageId(messageIdGenerator.nextId());
      log.debug("自动填充私聊消息 ID, packet={}", packet);
    }
    // 校验 message_id
    if (packet.getMessageId().length() != YeinGid.YEIN_GID_LENGTH) {
      return Mono.just(Result.error(BizCodes.C3600.getCode(), "[message_id]不能为空且必须是一个长度为 15 的字符串"));
    }
    if (CharMatcher.whitespace().matchesAnyOf(packet.getMessageId())) {
      return Mono.just(Result.error(BizCodes.C3600.getCode(), "[message_id]不能包含空白字符"));
    }
    if (packet.getTo() <= 0) {
      return Mono.just(Result.error(BizCodes.C3600.getCode(), "[to]值不合法"));
    }
    if (Strings.isNullOrEmpty(packet.getContent())) {
      return Mono.just(Result.error(BizCodes.C3600.getCode(), "[content]值不能为空"));
    }
    if (MessageContentType.forCode(packet.getContentType()) == MessageContentType.UNRECOGNIZED) {
      return Mono.just(Result.error(BizCodes.C3600.getCode(), "[content_type]值不能为空"));
    }

    var entity = MessagePacketHelper.toMessageEntity(packet);
    var uids = List.of(packet.getFrom(), packet.getTo());
    return messageStorageService
        .store(entity, uids)
        .doFirst(() -> packetSender.sendToUser(List.of(packet.getTo()), packet))
        .thenReturn(Result.ok());
  }
}
