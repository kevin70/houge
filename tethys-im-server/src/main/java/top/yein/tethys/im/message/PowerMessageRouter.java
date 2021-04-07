package top.yein.tethys.im.message;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.constants.MessageKind;
import top.yein.tethys.message.MessageRouter;
import top.yein.tethys.packet.MessagePacket;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.session.SessionManager;

/**
 * 消息路由器实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class PowerMessageRouter implements MessageRouter {

  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;

  /**
   * @param sessionManager
   * @param sessionGroupManager
   */
  public PowerMessageRouter(
      SessionManager sessionManager, SessionGroupManager sessionGroupManager) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public Mono<Void> route(MessagePacket packet) {
    var kind = MessageKind.forCode(packet.getKind());
    Flux<Session> sessionFlux;
    if (kind.isGroup()) {
      sessionFlux = sessionGroupManager.findByGroupId(packet.getTo());
    } else {
      sessionFlux = sessionManager.findByUid(packet.getTo());
    }

    // Netty ByteBuf 提供者
    var byteBufProvider = new PacketByteBufProvider(packet);
    // 释放 Netty ByteBuf 回调函数
    Runnable releaseByteBufFunc =
        () -> {
          var byteBuf = byteBufProvider.obtainByteBuf();
          if (byteBuf == null) {
            return;
          }
          if (!byteBuf.release()) {
            log.error(
                "释放 ByteBuf 失败[packet={}, refCnt={}] {}",
                packet,
                byteBuf.refCnt(),
                byteBuf.touch());
          }
        };
    return sessionFlux
        .flatMap(session -> session.send(Mono.just(byteBufProvider.retainedByteBuf())))
        .doOnCancel(releaseByteBufFunc)
        .doOnTerminate(releaseByteBufFunc)
        .then();
  }
}
