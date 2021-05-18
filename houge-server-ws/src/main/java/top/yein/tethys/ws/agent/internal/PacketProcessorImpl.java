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
package top.yein.tethys.ws.agent.internal;

import io.netty.buffer.ByteBufAllocator;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.yein.tethys.grpc.AgentPb;
import top.yein.tethys.ws.session.Session;
import top.yein.tethys.ws.session.SessionGroupManager;
import top.yein.tethys.ws.session.SessionManager;
import top.yein.tethys.ws.agent.PacketProcessor;

/** @author KK (kzou227@qq.com) */
public class PacketProcessorImpl implements PacketProcessor {

  private static final Logger log = LogManager.getLogger();

  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;

  @Inject
  public PacketProcessorImpl(
      SessionManager sessionManager, SessionGroupManager sessionGroupManager) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public void process(AgentPb.PacketMixin packetMixin) {
    switch (packetMixin.getTypeValue()) {
      case AgentPb.PacketMixinType.USER_VALUE:
        {
          if (packetMixin.getToCount() == 1) {
            invoke(sessionManager.findByUid(packetMixin.getTo(0)), packetMixin);
            return;
          }
          invoke(
              Flux.fromIterable(packetMixin.getToList()).flatMap(sessionManager::findByUid),
              packetMixin);
        }
        break;
      case AgentPb.PacketMixinType.GROUP_VALUE:
        {
          if (packetMixin.getToCount() == 1) {
            invoke(sessionGroupManager.findByGroupId(packetMixin.getTo(0)), packetMixin);
            return;
          }

          invoke(
              Flux.fromIterable(packetMixin.getToList())
                  .flatMap(sessionGroupManager::findByGroupId),
              packetMixin);
          break;
        }
      case AgentPb.PacketMixinType.ALL_VALUE:
        invoke(sessionManager.all(), packetMixin);
        break;
      default:
        log.error("不支持的消息转发类型 {}", packetMixin);
    }
  }

  private void invoke(Flux<Session> sessionFlux, AgentPb.PacketMixin packetMixin) {
    var src = packetMixin.getDataBytes().asReadOnlyByteBuffer();
    var byteBuf = ByteBufAllocator.DEFAULT.buffer(src.capacity());
    byteBuf.writeBytes(src);
    var byteBufMono = Mono.fromSupplier(() -> byteBuf.retainedDuplicate());
    sessionFlux
        .flatMap(session -> session.send(byteBufMono).thenReturn(session))
        .doOnNext(session -> log.debug("已向用户 {} 推送消息 {}", session, packetMixin))
        .onErrorContinue((ex, o) -> log.error("向 {} 发送消息出现错误", o, ex))
        .doFinally(unused -> byteBuf.release())
        .subscribeOn(Schedulers.parallel())
        .subscribe();
  }
}
