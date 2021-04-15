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

import java.util.function.Predicate;
import javax.inject.Inject;
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
    Flux<Session> sessionFlux;
    if (kind.isGroup()) {
      sessionFlux = sessionGroupManager.findByGroupId(packet.getTo());
    } else {
      sessionFlux = sessionManager.findByUid(packet.getTo());
    }

    // Netty ByteBuf 提供者
    var byteBufProvider = new PacketByteBufProvider(packet);
    Runnable releaseByteBufFunc = releaseByteBuf(byteBufProvider);

    return sessionFlux
        .filter(p)
        .flatMap(session -> session.send(Mono.just(byteBufProvider.retainedByteBuf())))
        .doOnCancel(releaseByteBufFunc)
        .doOnTerminate(releaseByteBufFunc)
        .then();
  }

  // 释放 Netty ByteBuf 回调函数
  private Runnable releaseByteBuf(PacketByteBufProvider byteBufProvider) {
    return () -> {
      var byteBuf = byteBufProvider.obtainByteBuf();
      if (byteBuf == null) {
        return;
      }
      if (!byteBuf.release()) {
        log.error(
            "释放 ByteBuf 失败[packet={}, refCnt={}] {}",
            byteBufProvider.getPacket(),
            byteBuf.refCnt(),
            byteBuf.touch());
      }
    };
  }
}
