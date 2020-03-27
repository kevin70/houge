package io.zhudy.xim.router;

import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.session.Session;
import java.util.function.BiFunction;
import reactor.core.publisher.Mono;

/** @author Kevin Zou (kevinz@weghst.com) */
public interface PacketRouter extends BiFunction<Session, Mono<Packet>, Mono<Void>> {

  /** */
  String CONTEXT_PACKET_BYTE_BUF = "packet.byteBuf";

  /**
   * @param session
   * @param packetMono
   * @return
   */
  @Override
  Mono<Void> apply(Session session, Mono<Packet> packetMono);
}
