package top.yein.tethys.im.handler;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.tethys.core.MessageProperties;
import top.yein.tethys.entity.GroupMessage;
import top.yein.tethys.id.MessageIdGenerator;
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
@Log4j2
public class GroupMessageHandler implements PacketHandler<GroupMessagePacket> {

  private final SessionGroupManager sessionGroupManager;
  private final GroupMessageRepository groupMessageRepository;
  private final MessageProperties messageProperties;
  private final MessageIdGenerator messageIdGenerator;

  /**
   * 构造函数.
   *
   * @param sessionGroupManager 群组会话管理对象
   * @param groupMessageRepository 群组消息存储器
   * @param messageProperties 聊天消息静态配置
   * @param messageIdGenerator 消息 ID 生成器
   */
  @Inject
  public GroupMessageHandler(
      SessionGroupManager sessionGroupManager,
      GroupMessageRepository groupMessageRepository,
      MessageProperties messageProperties,
      MessageIdGenerator messageIdGenerator) {
    this.sessionGroupManager = sessionGroupManager;
    this.groupMessageRepository = groupMessageRepository;
    this.messageProperties = messageProperties;
    this.messageIdGenerator = messageIdGenerator;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull GroupMessagePacket packet) {
    if (packet.getMsgId() == null && messageProperties.isAutofillId()) {
      packet.setMsgId(messageIdGenerator.nextId());
      log.debug("自动填充群组消息 ID, packet={}, session={}", packet, session);
    }

    // 校验消息
    MessagePacketChecker.check(packet);
    var from =
        Optional.ofNullable(packet.getFrom())
            .orElseGet(
                () -> {
                  packet.setFrom(session.uid());
                  return session.uid();
                });
    var to = packet.getTo();

    var p1 =
        sessionGroupManager
            .findByGroupId(to)
            .filter(toSession -> toSession != session)
            .flatMap(toSession -> toSession.sendPacket(packet));

    // 存储的消息实体
    var entity =
        GroupMessage.builder()
            .id(packet.getMsgId())
            .senderId(from)
            .groupId(to)
            .kind(packet.getKind())
            .content(packet.getContent())
            .url(packet.getUrl())
            .customArgs(packet.getCustomArgs())
            .build();
    var p2 = groupMessageRepository.insert(entity);
    return p2.thenMany(p1).then();
  }
}
