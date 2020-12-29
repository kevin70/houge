package top.yein.tethys.im.handler;

import javax.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.GroupMessagePacket;
import top.yein.tethys.packet.GroupSubscribePacket;
import top.yein.tethys.packet.GroupUnsubscribePacket;
import top.yein.tethys.packet.Packet;
import top.yein.tethys.packet.PingPacket;
import top.yein.tethys.packet.PongPacket;
import top.yein.tethys.packet.PrivateMessagePacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.session.SessionManager;

/**
 * @author KK (kzou227@qq.com)
 * @date 2020-12-29 15:43
 */
@Log4j2
public class BasisPacketHandler implements PacketHandler {

  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;

  public BasisPacketHandler(
      SessionManager sessionManager, SessionGroupManager sessionGroupManager) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
  }

  @Nonnull
  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull Packet packet) {
    if (packet instanceof PrivateMessagePacket) {
      return handlePrivateMessage(session, (PrivateMessagePacket) packet);
    } else if (packet instanceof GroupMessagePacket) {
      return handleGroupMessage(session, (GroupMessagePacket) packet);
    } else if (packet instanceof GroupSubscribePacket) {
      return handleGroupSubscribe(session, (GroupSubscribePacket) packet);
    } else if (packet instanceof GroupUnsubscribePacket) {
      return handleGroupUnsubscribe(session, (GroupUnsubscribePacket) packet);
    } else if (packet instanceof PingPacket) {
      return handlePing(session, (PingPacket) packet);
    }

    // FIXME 响应错误消息
    log.error("未找到 Packet[@ns={}] 实现 {}", packet.getNs(), packet);
    return Mono.empty();
  }
  /**
   * @param session
   * @param packet
   * @return
   */
  protected Mono<Void> handlePrivateMessage(
      final Session session, final PrivateMessagePacket packet) {
    //    return messageRouter.route(packet);
    return Mono.empty();
  }

  /**
   * @param session
   * @param packet
   * @return
   */
  protected Mono<Void> handleGroupMessage(final Session session, final GroupMessagePacket packet) {
    return Mono.empty();
  }

  /**
   * 订阅指定的群组消息.
   *
   * @param session 会话
   * @param packet 消息包
   * @return RS
   */
  protected Mono<Void> handleGroupSubscribe(
      final Session session, final GroupSubscribePacket packet) {
    var groupIds = packet.getGroupIds();
    if (groupIds == null || groupIds.isEmpty()) {
      return Mono.empty();
    }
    return sessionGroupManager.subGroups(session, groupIds);
  }

  /**
   * 取消指定的群组消息订阅.
   *
   * @param session 会话
   * @param packet 消息包
   * @return RS
   */
  protected Mono<Void> handleGroupUnsubscribe(
      final Session session, final GroupUnsubscribePacket packet) {
    var groupIds = packet.getGroupIds();
    if (groupIds == null || groupIds.isEmpty()) {
      return Mono.empty();
    }
    return sessionGroupManager.unsubGroups(session, groupIds);
  }

  /**
   * 会话心跳处理.
   *
   * @param session 会话
   * @param packet 心跳包
   * @return RS
   */
  protected Mono<Void> handlePing(final Session session, final PingPacket packet) {
    log.debug(
        "心跳 PingPacket[@ns={}] Session[sessionId={}, uid={}]",
        session.sessionId(),
        session.uid(),
        packet.getNs());
    final var emptyMono =
        Mono.defer(
            () -> {
              // 服务器会话丢失，断开客户端链接并响应错误信息
              log.error(
                  "PingPacket 服务器会话丢失 Session[sessionId={}, uid={}]",
                  session.sessionId(),
                  session.uid());
              return session
                  .close()
                  .doOnSuccess(
                      unused ->
                          log.info(
                              "PingPacket 服务器会话丢失关闭链接成功 Session[sessionId={}, uid={}]",
                              session.sessionId(),
                              session.uid()))
                  .doOnError(
                      e ->
                          log.error(
                              "PingPacket 服务器会话丢失关闭链接异常 Session[sessionId={}, uid={}]",
                              session.sessionId(),
                              session.uid(),
                              e));
            });
    return sessionManager
        .findById(session.sessionId())
        .thenEmpty(emptyMono)
        .flatMap(unused -> session.sendPacket(new PongPacket()));
  }
}
