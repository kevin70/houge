package top.yein.tethys.im.handler;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.tethys.core.MessageProperties;
import top.yein.tethys.entity.Message;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.im.handler.internal.MessagePacketChecker;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.GroupMessagePacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.storage.MessageDao;
import top.yein.tethys.storage.query.GroupQueryDao;

/**
 * 群组消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class GroupMessageHandler implements PacketHandler<GroupMessagePacket> {

  private final MessageProperties messageProperties;
  private final MessageIdGenerator messageIdGenerator;
  private final SessionGroupManager sessionGroupManager;

  private final GroupQueryDao groupQueryDao;
  private final MessageDao messageDao;

  /**
   * 构造函数.
   *
   * @param messageProperties 聊天消息静态配置
   * @param messageIdGenerator 消息 ID 生成器
   * @param sessionGroupManager 群组会话管理对象
   * @param messageDao 消息存储数据访问对象
   * @param groupQueryDao 群组查询数据访问对象
   */
  @Inject
  public GroupMessageHandler(
      MessageProperties messageProperties,
      MessageIdGenerator messageIdGenerator,
      SessionGroupManager sessionGroupManager,
      MessageDao messageDao,
      GroupQueryDao groupQueryDao) {
    this.sessionGroupManager = sessionGroupManager;
    this.messageDao = messageDao;
    this.messageProperties = messageProperties;
    this.messageIdGenerator = messageIdGenerator;
    this.groupQueryDao = groupQueryDao;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull GroupMessagePacket packet) {
    if (packet.getMessageId() == null && messageProperties.isAutofillId()) {
      packet.setMessageId(messageIdGenerator.nextId());
      log.debug("自动填充群组消息 ID, packet={}, session={}", packet, session);
    }

    var groupId = packet.getTo();
    // 校验消息
    MessagePacketChecker.check(packet);
    var from = Optional.ofNullable(packet.getFrom()).orElseGet(session::uid);

    var sendMono =
        sessionGroupManager
            .findByGroupId(groupId)
            .filter(toSession -> toSession != session)
            .flatMap(toSession -> toSession.sendPacket(packet));

    // 存储的消息实体
    var entity =
        Message.builder()
            .id(packet.getMessageId())
            .senderId(from)
            .groupId(groupId)
            .kind(Message.KIND_GROUP)
            .content(packet.getContent())
            .contentKind(packet.getContentKind())
            .url(packet.getUrl())
            .customArgs(packet.getCustomArgs())
            .unread(Message.MESSAGE_UNREAD)
            .build();
    var storageMono =
        groupQueryDao
            .queryMembersUid(groupId)
            .collectList()
            .flatMap(memberIds -> messageDao.insert(entity, memberIds));
    return storageMono.thenMany(sendMono).then();
  }
}
