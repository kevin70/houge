/**
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

import io.zhudy.xim.BizCodeException;
import io.zhudy.xim.BizCodes;
import io.zhudy.xim.packet.GroupMsgPacket;
import io.zhudy.xim.packet.GroupSubPacket;
import io.zhudy.xim.packet.GroupUnsubPacket;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.packet.PrivateMsgPacket;
import io.zhudy.xim.session.Session;
import io.zhudy.xim.session.SessionGroupManager;
import io.zhudy.xim.session.SessionManager;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/**
 * 当前应用内 {@link Packet} 路由器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class LocalPacketRouter implements PacketRouter {

  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;

  @Inject
  public LocalPacketRouter(SessionManager sessionManager, SessionGroupManager sessionGroupManager) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public Mono<Void> route(Mono<Packet> packetMono) {
    return packetMono.flatMap(
        packet -> {
          log.debug("handle packet [packet={}]", packet);

          if (packet instanceof PrivateMsgPacket) {
            return handlePrivateMsg((PrivateMsgPacket) packet);
          }
          if (packet instanceof GroupMsgPacket) {
            return handleGroupMsg((GroupMsgPacket) packet);
          }
          if (packet instanceof GroupSubPacket) {
            return handleGroupSub((GroupSubPacket) packet);
          }
          if (packet instanceof GroupUnsubPacket) {
            return handleGroupUnsub((GroupUnsubPacket) packet);
          }

          log.error("Not found packet handler [ns={}, packet={}]", packet.getNs(), packet);
          var e =
              new BizCodeException(BizCodes.C404, "Not found packet handler")
                  .addContextValue("ns", packet.getNs())
                  .addContextValue("packet", packet);
          return Mono.error(e);
        });
  }

  protected Mono<Void> handlePrivateMsg(final PrivateMsgPacket packet) {
    return sessionManager
        .findByUid(packet.getTo())
        .flatMap(session -> session.sendPacket(packet))
        .then();
  }

  protected Mono<Void> handleGroupMsg(final GroupMsgPacket packet) {
    var sessions =
        Packet.GROUP_ID_ALL.equals(packet.getTo())
            ? sessionManager.all()
            : sessionGroupManager.findByGroupId(packet.getTo());
    return sessions.parallel().flatMap(session -> session.sendPacket(packet)).then();
  }

  protected Mono<Void> handleGroupSub(final GroupSubPacket packet) {
    return Mono.subscriberContext()
        .flatMap(
            context -> {
              if (!context.hasKey(Session.class)) {
                log.error("Not found session in context, packet={}", packet);
                throw new BizCodeException(BizCodes.C404, "Not found session in context");
              }

              final var session = context.get(Session.class);
              return sessionGroupManager.subGroups(session, packet.getGroupIds());
            });
  }

  protected Mono<Void> handleGroupUnsub(final GroupUnsubPacket packet) {
    return Mono.subscriberContext()
        .flatMap(
            context -> {
              if (!context.hasKey(Session.class)) {
                log.error("Not found session in context, packet={}", packet);
                throw new BizCodeException(BizCodes.C404, "Not found session in context");
              }

              final var session = context.get(Session.class);
              return sessionGroupManager.unsubGroups(session, packet.getGroupIds());
            });
  }
}
