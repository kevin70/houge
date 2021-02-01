package top.yein.tethys.im.handler;

import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.PrivateMessage;
import top.yein.tethys.im.handler.internal.MessagePacketChecker;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.PrivateMessagePacket;
import top.yein.tethys.repository.PrivateMessageRepository;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionManager;

/**
 * 私聊处理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class PrivateMessageHandler implements PacketHandler<PrivateMessagePacket> {

  private final SessionManager sessionManager;
  private final PrivateMessageRepository privateMessageRepository;

  /**
   * 构造函数.
   *
   * @param sessionManager 会话管理器
   * @param privateMessageRepository 私聊消息存储器
   */
  public PrivateMessageHandler(
      SessionManager sessionManager, PrivateMessageRepository privateMessageRepository) {
    this.sessionManager = sessionManager;
    this.privateMessageRepository = privateMessageRepository;
  }

  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull PrivateMessagePacket packet) {
    // 校验消息
    MessagePacketChecker.check(packet);
    var from = Optional.ofNullable(packet.getFrom()).orElseGet(session::uid);
    var to = packet.getTo();

    var p1 = sessionManager.findByUid(to).delayUntil(toSession -> toSession.sendPacket(packet));

    // 存储的消息实体
    var entity =
        PrivateMessage.builder()
            .id(packet.getMsgId())
            .senderId(from)
            .receiverId(to)
            .kind(packet.getKind())
            .content(packet.getContent())
            .url(packet.getUrl())
            .customArgs(packet.getCustomArgs())
            .build();
    var p2 = privateMessageRepository.store(entity);
    return Flux.zip(p1, p2).then();
  }
}
