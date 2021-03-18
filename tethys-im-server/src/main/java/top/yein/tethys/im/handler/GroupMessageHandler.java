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
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.storage.MessageDao;

/**
 * 群组消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class GroupMessageHandler implements PacketHandler<GroupMessagePacket> {

  private final SessionGroupManager sessionGroupManager;
  private final MessageDao messageDao;
  private final MessageProperties messageProperties;
  private final MessageIdGenerator messageIdGenerator;

  /**
   * 构造函数.
   *
   * @param sessionGroupManager 群组会话管理对象
   * @param messageDao
   * @param messageProperties 聊天消息静态配置
   * @param messageIdGenerator 消息 ID 生成器
   */
  @Inject
  public GroupMessageHandler(
    SessionGroupManager sessionGroupManager,
    MessageDao messageDao,
    MessageProperties messageProperties,
    MessageIdGenerator messageIdGenerator) {
    this.sessionGroupManager = sessionGroupManager;
    this.messageDao = messageDao;
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
                  // FIXME
                  packet.setFrom(session.authContext().originUid());
                  return session.authContext().originUid();
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
    // FIXME 消息存储重构
//    var p2 = groupMessageRepository.insert(entity);
    var p2 = Mono.empty();
    return p2.thenMany(p1).then();
  }
}
