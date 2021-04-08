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
package top.yein.tethys.service;

import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.constants.MessageKind;
import top.yein.tethys.packet.MessagePacket;
import top.yein.tethys.storage.MessageDao;
import top.yein.tethys.storage.entity.Message;
import top.yein.tethys.storage.query.GroupQueryDao;

/**
 * 消息存储服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class MessageStorageServiceImpl implements MessageStorageService {

  private final MessageDao messageDao;
  private final GroupQueryDao groupQueryDao;

  /**
   * 可被 IoC 容器使用的构造函数.
   *
   * @param messageDao 消息数据访问对象
   * @param groupQueryDao 群组查询数据访问对象
   */
  @Inject
  public MessageStorageServiceImpl(MessageDao messageDao, GroupQueryDao groupQueryDao) {
    this.messageDao = messageDao;
    this.groupQueryDao = groupQueryDao;
  }

  @Override
  public Mono<Void> store(MessagePacket packet) {
    var kind = MessageKind.forCode(packet.getKind());

    Flux<Long> uidFlux;
    var builder =
        Message.builder()
            .id(packet.getMessageId())
            .senderId(packet.getFrom())
            .receiverId(kind.isGroup() ? null : packet.getTo())
            .groupId(kind.isGroup() ? packet.getTo() : null)
            .kind(packet.getKind())
            .content(packet.getContent())
            .contentType(packet.getContentType())
            .extraArgs(packet.getExtraArgs());
    if (kind == MessageKind.P_MESSAGE) {
      uidFlux = Flux.just(packet.getFrom(), packet.getTo());
      builder.receiverId(packet.getTo());
    } else if (kind == MessageKind.G_MESSAGE) {
      uidFlux = groupQueryDao.queryUidByGid(packet.getTo());
      builder.groupId(packet.getTo());
    } else {
      uidFlux = Flux.empty();
      log.warn("已忽略不支持的消息存储类型 kind={} packet={}", kind, packet);
    }

    return uidFlux.collectList().flatMap(uids -> messageDao.insert(builder.build(), uids));
  }
}
