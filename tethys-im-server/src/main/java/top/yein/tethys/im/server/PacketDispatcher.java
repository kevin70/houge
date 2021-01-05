package top.yein.tethys.im.server;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.packet.ErrorPacket;
import top.yein.tethys.packet.Packet;
import top.yein.tethys.session.Session;

/**
 * Packet 分发器.
 *
 * @author KK (kzou227@qq.com)
 * @date 2020-12-29 15:43
 */
@Log4j2
public class PacketDispatcher {

  private final Map<String, PacketHandler> handlers;

  @Inject
  public PacketDispatcher(Map<String, PacketHandler> handlers) {
    this.handlers = handlers;
  }

  /**
   * Packet 分发器.
   *
   * @param session 登录会话
   * @param packet packet
   * @return RS
   */
  public Mono<Void> dispatch(@Nonnull Session session, @Nonnull Packet packet) {
    var handler = handlers.get(packet.getNs());
    if (handler == null) {
      log.error("未找到 Packet[@ns={}] 实现 {}", packet.getNs(), packet);
      var error = new ErrorPacket("未找到 [@ns=" + packet.getNs() + "] 处理器", packet.toString());
      return session.sendPacket(error);
    }
    log.debug("{} 发送消息:{}{}", session, System.lineSeparator(), packet);
    return handler.handle(session, packet);
  }
}
