/*
 * Copyright 2019-2020 the original author or authors
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
package top.yein.tethys.core.session;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;
import top.yein.tethys.auth.AuthContext;
import top.yein.tethys.packet.Packet;
import top.yein.tethys.session.Session;
import top.yein.tethys.util.JsonUtils;

/**
 * 默认会话实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public final class DefaultSession implements Session {

  private static final ObjectWriter OBJECT_WRITER =
      JsonUtils.objectMapper().writerFor(Packet.class);

  final String sessionId;
  final WebsocketInbound inbound;
  final WebsocketOutbound outbound;
  final AuthContext authContext;
  final Set<Long> subGroupIds;

  private final Sinks.Empty<Void> closeSink = Sinks.empty();
  private final AtomicBoolean closed = new AtomicBoolean(false);

  /**
   * 构造默认会话.
   *
   * @param sessionId 会话 ID
   * @param inbound 输入
   * @param outbound 输出
   * @param authContext 认证上下文
   */
  public DefaultSession(
      String sessionId,
      WebsocketInbound inbound,
      WebsocketOutbound outbound,
      AuthContext authContext) {
    requireNonNull(inbound, "inbound 不能为 null");
    requireNonNull(outbound, "outbound 不能为 null");
    requireNonNull(authContext, "authContext 不能为 null");

    this.sessionId = sessionId;
    this.inbound = inbound;
    this.outbound = outbound;
    this.authContext = authContext;

    // 连接关闭
    this.inbound.withConnection(
        conn ->
            conn.onDispose()
                .doOnTerminate(() -> closed.set(true))
                .subscribe(
                    unused -> closeSink.tryEmitEmpty(),
                    closeSink::tryEmitError,
                    closeSink::tryEmitEmpty));
    this.subGroupIds = new ConcurrentSkipListSet<>();
  }

  @Override
  public String sessionId() {
    return sessionId;
  }

  @Override
  public AuthContext authContext() {
    return authContext;
  }

  @Override
  public boolean isClosed() {
    return closed.get();
  }

  @Override
  public Set<Long> subGroupIds() {
    return subGroupIds;
  }

  @Override
  public Mono<Void> sendPacket(Publisher<Packet> source) {
    return Mono.from(source)
        .map(
            packet -> {
              try {
                var buf = outbound.alloc().directBuffer();
                OutputStream out = new ByteBufOutputStream(buf);
                OBJECT_WRITER.writeValue(out, packet);
                return buf;
              } catch (IOException e) {
                var message =
                    log.getMessageFactory()
                        .newMessage(
                            "Packet JSON 序列化错误[sessionId={}, uid={}]{}Packet:{}{}",
                            sessionId(),
                            uid(),
                            System.lineSeparator(),
                            System.lineSeparator(),
                            packet)
                        .getFormattedMessage();
                log.error(message, e);
                throw new IllegalStateException(message, e);
              }
            })
        .transform(this::send);
  }

  @Override
  public Mono<Void> send(Publisher<ByteBuf> source) {
    return Mono.from(source)
        .transform(p -> outbound.send(p).then());
  }

  @Override
  public Mono<Void> close() {
    return Mono.defer(
        () -> {
          if (closed.compareAndSet(false, true)) {
            // 触发 onClose 事件
            return outbound.sendClose();
          }
          return Mono.empty();
        });
  }

  @Override
  public Mono<Void> onClose() {
    return closeSink.asMono();
  }

  @Override
  public String toString() {
    HttpServerRequest request = (HttpServerRequest) inbound;
    var clientIp =
        Optional.ofNullable(request.remoteAddress())
            .map(InetSocketAddress::getAddress)
            .map(InetAddress::getHostAddress)
            .orElse("[UNKNOWN]");
    return new StringBuilder()
        .append("Session{")
        .append("sessionId=")
        .append(sessionId())
        .append(", ")
        .append("uid=")
        .append(uid())
        .append(", ")
        .append("clientIp=")
        .append(clientIp)
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
}
