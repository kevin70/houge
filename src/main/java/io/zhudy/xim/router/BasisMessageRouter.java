/*
 * Copyright 2019-2020 the original author or authors
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
package io.zhudy.xim.router;

import io.zhudy.xim.packet.MsgPacket;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.packet.PrivateMsgPacket;
import io.zhudy.xim.session.SessionGroupManager;
import io.zhudy.xim.session.SessionManager;
import javax.inject.Inject;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 基础的消息路由器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class BasisMessageRouter implements MessageRouter {

  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;

  @Inject
  public BasisMessageRouter(
      SessionManager sessionManager, SessionGroupManager sessionGroupManager) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public Mono<Void> route(final MsgPacket packet) {
    if (packet instanceof PrivateMsgPacket) {
      return handlePrivateMsg(packet);
    }
    return handleGroupMsg(packet);
  }

  protected Mono<Void> handlePrivateMsg(final MsgPacket packet) {
    return sessionManager
        .findByUid(packet.getTo())
        .flatMap(session -> session.sendPacket(packet))
        .then();
  }

  protected Mono<Void> handleGroupMsg(final MsgPacket packet) {
    var sessions =
        Packet.GROUP_ID_ALL.equals(packet.getTo())
            ? sessionManager.all()
            : sessionGroupManager.findByGroupId(packet.getTo());
    return sessions
        .parallel()
        .filter(session -> !Objects.equals(packet.getFrom(), session.uid()))
        .flatMap(session -> session.sendPacket(packet))
        .then();
  }
}
