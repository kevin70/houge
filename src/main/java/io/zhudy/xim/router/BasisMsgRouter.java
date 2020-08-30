package io.zhudy.xim.router;

import io.zhudy.xim.packet.MsgPacket;
import reactor.core.publisher.Mono;

/**
 * 基础的消息路由器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class BasisMsgRouter implements MsgRouter {

  @Override
  public Mono<Void> route(MsgPacket packet) {
    return null;
  }
}
