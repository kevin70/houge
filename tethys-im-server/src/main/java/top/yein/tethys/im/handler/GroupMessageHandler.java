package top.yein.tethys.im.handler;

import java.util.Optional;
import javax.annotation.Nonnull;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.GroupMessage;
import top.yein.tethys.im.handler.internal.MessagePacketChecker;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.GroupMessagePacket;
import top.yein.tethys.repository.GroupMessageRepository;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupManager;

/**
 * 群组消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupMessageHandler implements PacketHandler<GroupMessagePacket> {

  private final SessionGroupManager sessionGroupManager;
  private final GroupMessageRepository groupMessageRepository;

  /**
   * 构造函数.
   *
   * @param sessionGroupManager 群组会话管理对象
   * @param groupMessageRepository 群组消息存储器
   */
  public GroupMessageHandler(
      SessionGroupManager sessionGroupManager, GroupMessageRepository groupMessageRepository) {
    this.sessionGroupManager = sessionGroupManager;
    this.groupMessageRepository = groupMessageRepository;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull GroupMessagePacket packet) {
    // 校验消息
    MessagePacketChecker.check(packet);
    var from = Optional.ofNullable(packet.getFrom()).orElseGet(session::uid);

    // 存储的消息实体
    GroupMessage entity = new GroupMessage();
    entity.setId(packet.getMsgId());
    entity.setSenderId(from);

    var groupId = packet.getTo();
    return sessionGroupManager
        .findByGroupId(groupId)
        .filter(toSession -> toSession != session)
        .flatMap(toSession -> toSession.sendPacket(packet))
        .then();
  }
}
