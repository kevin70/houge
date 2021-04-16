/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.yein.tethys.im.message;

import io.netty.buffer.ByteBuf;
import io.netty.util.IllegalReferenceCountException;
import java.io.IOException;
import java.util.function.Predicate;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.constants.MessageKind;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.util.PacketUtils;
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
   * 能被 IoC 容器使用的构造函数.
   *
   * @param sessionManager 会话管理
   * @param sessionGroupManager 会话群组管理
   */
  @Inject
  public PowerMessageRouter(
      SessionManager sessionManager, SessionGroupManager sessionGroupManager) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public Mono<Void> route(MessagePacket packet, Predicate<Session> p) {
    var kind = MessageKind.forCode(packet.getKind());
    if (kind.isGroup()) {
      return groupPacket(packet, p);
    } else {
      return singlePacket(packet);
    }
  }

  private Mono<Void> singlePacket(MessagePacket packet) {
    return sessionManager
        .findByUid(packet.getTo())
        .flatMap(session -> session.sendPacket(packet))
        .then();
  }

  private Mono<Void> groupPacket(MessagePacket packet, Predicate<Session> filter) {
    var byteBuf = toByteBuf(packet);
    return sessionGroupManager
        .findByGroupId(packet.getTo())
        .filter(filter)
        .flatMap(session -> session.send(Mono.just(byteBuf.retainedDuplicate())))
        .doFinally(
            signalType -> {
              try {
                if (!byteBuf.release()) {
                  log.error(
                      "释放ByteBuf失败 signalType={} packet={} refCnt={}",
                      signalType,
                      packet,
                      byteBuf.refCnt());
                }
              } catch (IllegalReferenceCountException e) {
                log.error(
                    "释放ByteBuf异常 signalType={} packet={} refCnt={}",
                    signalType,
                    packet,
                    byteBuf.refCnt());
              }
            })
        .then();
  }

  private ByteBuf toByteBuf(MessagePacket packet) {
    try {
      return PacketUtils.toByteBuf(packet);
    } catch (IOException e) {
      throw new BizCodeException(BizCodes.C3601);
    }
  }
}
