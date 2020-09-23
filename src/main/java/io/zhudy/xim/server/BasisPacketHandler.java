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
package io.zhudy.xim.server;

import io.zhudy.xim.BizCodeException;
import io.zhudy.xim.BizCodes;
import io.zhudy.xim.packet.GroupMsgPacket;
import io.zhudy.xim.packet.GroupSubPacket;
import io.zhudy.xim.packet.GroupUnsubPacket;
import io.zhudy.xim.packet.Namespaces;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.packet.PrivateMsgPacket;
import io.zhudy.xim.router.MessageRouter;
import io.zhudy.xim.session.Session;
import io.zhudy.xim.session.SessionGroupManager;
import io.zhudy.xim.session.SessionManager;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/**
 * 基础的 Packet 逻辑处理器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class BasisPacketHandler implements PacketHandler {

  // TODO 后期修改
  @SuppressWarnings("unused")
  private final SessionManager sessionManager;

  private final SessionGroupManager sessionGroupManager;
  private final MessageRouter messageRouter;

  @Inject
  public BasisPacketHandler(
      SessionManager sessionManager,
      SessionGroupManager sessionGroupManager,
      MessageRouter messageRouter) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
    this.messageRouter = messageRouter;
  }

  @Override
  public Mono<Void> handle(Packet packet, Session session) {
    final Mono<Void> rs;
    switch (packet.getNs()) {
      case Namespaces.PRIVATE_MSG:
        {
          PrivateMsgPacket p = (PrivateMsgPacket) packet;
          rs = handlePrivateMsg(p, session);
          break;
        }
      case Namespaces.GROUP_MSG:
        {
          GroupMsgPacket p = (GroupMsgPacket) packet;
          rs = handleGroupMsg(p, session);
          break;
        }
      case Namespaces.GROUP_SUBSCRIBE:
        {
          rs = handleGroupSub((GroupSubPacket) packet, session);
          break;
        }
      case Namespaces.GROUP_UNSUBSCRIBE:
        {
          rs = handleGroupUnsub((GroupUnsubPacket) packet, session);
          break;
        }
      default:
        {
          // handle ping
          rs = handlePing(packet, session);
          break;
        }
    }
    return rs;
  }

  /**
   * @param packet
   * @param session
   * @return
   */
  protected Mono<Void> handlePrivateMsg(final PrivateMsgPacket packet, final Session session) {
    return messageRouter.route(packet);
  }

  /**
   * @param packet
   * @param session
   * @return
   */
  protected Mono<Void> handleGroupMsg(final GroupMsgPacket packet, final Session session) {
    return messageRouter.route(packet);
  }

  /**
   * 会话心跳处理.
   *
   * @param packet 心跳包
   * @param session 会话
   * @return empty 发射器
   */
  protected Mono<Void> handlePing(final Packet packet, final Session session) {
    // FIXME
    return null;
  }

  /**
   * @param packet
   * @param session
   * @return
   */
  protected Mono<Void> handleGroupSub(final GroupSubPacket packet, final Session session) {
    return sessionGroupManager.subGroups(session, packet.getGroupIds());
  }

  /**
   * @param packet
   * @param session
   * @return
   */
  protected Mono<Void> handleGroupUnsub(final GroupUnsubPacket packet, final Session session) {
    return Mono.subscriberContext()
        .flatMap(
            context -> {
              if (!context.hasKey(Session.class)) {
                log.error("Not found session in context, packet={}", packet);
                return Mono.error(
                    new BizCodeException(BizCodes.C404, "Not found session in context"));
              }

              return sessionGroupManager.unsubGroups(session, packet.getGroupIds());
            });
  }
}
