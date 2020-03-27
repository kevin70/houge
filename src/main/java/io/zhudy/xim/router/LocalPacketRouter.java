package io.zhudy.xim.router;

import io.zhudy.xim.BizCodeException;
import io.zhudy.xim.BizCodes;
import io.zhudy.xim.packet.GroupMsgPacket;
import io.zhudy.xim.packet.GroupSubPacket;
import io.zhudy.xim.packet.GroupUnsubPacket;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.packet.PrivateMsgPacket;
import io.zhudy.xim.session.SessionGroupManager;
import io.zhudy.xim.session.Session;
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
  public Mono<Void> apply(Session session, Mono<Packet> packetMono) {
    return packetMono.flatMap(
        packet -> {
          log.debug("uid: {} handle packet [packet={}]", session.uid(), packet);

          if (packet instanceof PrivateMsgPacket) {
            return handlePrivateMsg(session, (PrivateMsgPacket) packet);
          }
          if (packet instanceof GroupMsgPacket) {
            return handleGroupMsg(session, (GroupMsgPacket) packet);
          }
          if (packet instanceof GroupSubPacket) {
            return handleGroupSub(session, (GroupSubPacket) packet);
          }
          if (packet instanceof GroupUnsubPacket) {
            return handleGroupUnsub(session, (GroupUnsubPacket) packet);
          }

          log.error(
              "uid: {} Not found packet handler [ns={}, class={}, packet={}]",
              session.uid(),
              packet.getNs(),
              packet.getClass(),
              packet);
          var e =
              new BizCodeException(BizCodes.C404, "Not found packet handler")
                  .addContextValue("ns", packet.getNs())
                  .addContextValue("packetClass", packet.getClass())
                  .addContextValue("uid", session.uid());
          return Mono.error(e);
        });
  }

  protected Mono<Void> handlePrivateMsg(Session session, PrivateMsgPacket packet) {
    return sessionManager
        .findByUid(packet.getTo())
        .flatMap(
            toSession -> {
              //
              return toSession.sendPacket(packet);
            })
        .then();
  }

  protected Mono<Void> handleGroupMsg(Session session, GroupMsgPacket packet) {
    return Mono.empty();
  }

  protected Mono<Void> handleGroupSub(Session session, GroupSubPacket packet) {
    return Mono.empty();
  }

  protected Mono<Void> handleGroupUnsub(Session session, GroupUnsubPacket packet) {
    return Mono.empty();
  }
}
