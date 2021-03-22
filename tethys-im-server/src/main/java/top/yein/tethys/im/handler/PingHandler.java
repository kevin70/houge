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
package top.yein.tethys.im.handler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
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
public class PingHandler implements PacketHandler<PingPacket> {

  private final SessionManager sessionManager;

  @Inject
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
