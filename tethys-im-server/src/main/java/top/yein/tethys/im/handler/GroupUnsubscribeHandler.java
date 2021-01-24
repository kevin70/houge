package top.yein.tethys.im.handler;

import javax.annotation.Nonnull;
import reactor.core.publisher.Mono;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.GroupUnsubscribePacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupManager;

/**
 * 取消订阅群组消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupUnsubscribeHandler implements PacketHandler<GroupUnsubscribePacket> {

  private final SessionGroupManager sessionGroupManager;

  public GroupUnsubscribeHandler(SessionGroupManager sessionGroupManager) {
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull GroupUnsubscribePacket packet) {
    return sessionGroupManager.unsubGroups(session, packet.getGroupIds());
  }
}
