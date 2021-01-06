package top.yein.tethys.im.handler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.GroupMessagePacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupManager;

/**
 * 群组消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupMessageHandler implements PacketHandler<GroupMessagePacket> {

  private final SessionGroupManager sessionGroupManager;

  @Inject
  public GroupMessageHandler(SessionGroupManager sessionGroupManager) {
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull GroupMessagePacket packet) {
    var groupId = packet.getTo();
    return sessionGroupManager
        .findByGroupId(groupId)
        .flatMap(toSession -> toSession.sendPacket(packet))
        .then();
  }
}
