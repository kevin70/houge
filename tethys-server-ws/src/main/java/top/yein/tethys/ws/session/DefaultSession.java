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
package top.yein.tethys.ws.session;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

/**
 * 默认会话实现.
 *
 * @author KK (kzou227@qq.com)
 */
public final class DefaultSession implements Session {

  private static final AtomicLong SESSION_ID_SEQ = new AtomicLong();

  final WebsocketInbound inbound;
  final WebsocketOutbound outbound;
  final long uid;
  final String token;
  final long sessionId;
  final Set<Long> subGroupIds;

  // 客户端IP临时变量
  private String clientIp;

  /**
   * 使用用户认证信息与WebSocket输入输出流构造对象.
   *
   * @param inbound WS输入流
   * @param outbound WS输出流
   * @param uid 认证用户ID
   * @param token 访问令牌
   */
  public DefaultSession(
      WebsocketInbound inbound, WebsocketOutbound outbound, long uid, String token) {
    this.inbound = inbound;
    this.outbound = outbound;
    this.uid = uid;
    this.token = token;
    this.sessionId = SESSION_ID_SEQ.incrementAndGet();
    this.subGroupIds = new CopyOnWriteArraySet<>();
  }

  @Override
  public long sessionId() {
    return this.sessionId;
  }

  @Override
  public long uid() {
    return this.uid;
  }

  @Override
  public String token() {
    return this.token;
  }

  @Override
  public Set<Long> subGroupIds() {
    return this.subGroupIds;
  }

  @Override
  public boolean isClosed() {
    var closed = new boolean[1];
    outbound.withConnection(connection -> closed[0] = connection.isDisposed());
    return closed[0];
  }

  @Override
  public Mono<Void> send(Mono<ByteBuf> source) {
    return outbound.sendObject(source.map(TextWebSocketFrame::new)).then();
  }

  @Override
  public Mono<Void> close() {
    return outbound.sendClose();
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Session{")
        .append("sessionId=")
        .append(sessionId())
        .append(", ")
        .append("uid=")
        .append(uid())
        .append(", ")
        .append("clientIp=")
        .append(getClientIp())
        .append("}")
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultSession that = (DefaultSession) o;
    return sessionId == that.sessionId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sessionId);
  }

  private String getClientIp() {
    if (this.clientIp == null) {
      HttpServerRequest request = (HttpServerRequest) inbound;
      this.clientIp =
          Optional.ofNullable(request.remoteAddress())
              .map(InetSocketAddress::getAddress)
              .map(InetAddress::getHostAddress)
              .orElse("[UNKNOWN]");
    }
    return this.clientIp;
  }
}
