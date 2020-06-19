package io.zhudy.xim.router;

import io.zhudy.xim.packet.Packet;
import reactor.core.publisher.Mono;

/** @author Kevin Zou (kevinz@weghst.com) */
@FunctionalInterface
public interface PacketRouter {

  /** */
  String CONTEXT_PACKET_BYTE_BUF = "packet.byteBuf";

  /**
   * @param packetMono
   * @return
   */
  Mono<Void> route(Mono<Packet> packetMono);
}
