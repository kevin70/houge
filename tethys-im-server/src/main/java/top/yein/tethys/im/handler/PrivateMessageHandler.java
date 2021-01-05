package top.yein.tethys.im.handler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.PrivateMessagePacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionManager;

/**
 * @author KK (kzou227@qq.com)
 * @date 2021-01-05 16:12
 */
public class PrivateMessageHandler implements PacketHandler<PrivateMessagePacket> {

  private final SessionManager sessionManager;

  @Inject
  public PrivateMessageHandler(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull PrivateMessagePacket packet) {
    return sessionManager
        .findByUid(packet.getTo())
        .flatMap(toSession -> toSession.sendPacket(packet))
        .then();
  }
}
