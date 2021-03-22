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

import javax.annotation.Nonnull;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.GroupSubscribePacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupManager;

/**
 * 订阅群组消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupSubscribeHandler implements PacketHandler<GroupSubscribePacket> {

  private final SessionGroupManager sessionGroupManager;

  @Inject
  public GroupSubscribeHandler(SessionGroupManager sessionGroupManager) {
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull GroupSubscribePacket packet) {
    return sessionGroupManager.subGroups(session, packet.getGroupIds());
  }
}
