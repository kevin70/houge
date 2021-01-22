package top.yein.tethys.im.handler;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.PrivateMessage;
import top.yein.tethys.im.handler.internal.MessagePacketChecker;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.PrivateMessagePacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionManager;
import top.yein.tethys.repository.PrivateMessageRepository;

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
  @Inject
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

    // 存储的消息实体
    var entity = new PrivateMessage();
    entity.setId(packet.getMsgId());
    entity.setSenderId(from);
    entity.setReceiverId(packet.getTo());
    entity.setKind(packet.getKind());
    entity.setContent(packet.getContent());
    entity.setUrl(packet.getUrl());
    entity.setCustomArgs(packet.getCustomArgs());

    var p1 =
        sessionManager
            .findByUid(packet.getTo())
            .delayUntil(toSession -> toSession.sendPacket(packet));
    var p2 = privateMessageRepository.store(entity);
    return Flux.zip(p1, p2).then();
  }
}
