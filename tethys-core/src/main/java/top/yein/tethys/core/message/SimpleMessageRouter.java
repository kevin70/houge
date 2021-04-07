package top.yein.tethys.core.message;

import reactor.core.publisher.Mono;
import top.yein.tethys.constants.MessageKind;
import top.yein.tethys.message.MessageRouter;
import top.yein.tethys.packet.MessagePacket;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.session.SessionManager;

/**
 * 消息路由器实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class SimpleMessageRouter implements MessageRouter {

  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;

  /**
   * @param sessionManager
   * @param sessionGroupManager
   */
  public SimpleMessageRouter(
      SessionManager sessionManager, SessionGroupManager sessionGroupManager) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public Mono<Void> route(MessagePacket packet) {
    var kind = MessageKind.forCode(packet.getKind());
    if (kind.isGroup()) {
      return sessionGroupManager
          .findByGroupId(packet.getTo())
          .flatMap(session -> session.sendPacket(packet))
          .then();
    }
    return sessionManager
        .findByUid(packet.getFrom())
        .flatMap(session -> session.sendPacket(packet))
        .then();
  }
}
