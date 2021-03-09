package top.yein.tethys.im.handler;

import javax.annotation.Nonnull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.PingPacket;
import top.yein.tethys.packet.PongPacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionManager;

/**
 * 心跳处理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
@Component
public class PingHandler implements PacketHandler<PingPacket> {

  private final SessionManager sessionManager;

  public PingHandler(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  /**
   * 会话心跳处理.
   *
   * @param session 会话
   * @param packet 心跳包
   * @return RS
   */
  @Override
  public Mono<Void> handle(@Nonnull Session session, @Nonnull PingPacket packet) {
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
