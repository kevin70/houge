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
package io.zhudy.xim.session.impl;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.helper.PacketHelper;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.session.Session;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.requireNonNull;

/**
 * 默认会话实现.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public final class DefaultSession implements Session {

  final String sessionId;
  final String uid;
  final WebsocketInbound inbound;
  final WebsocketOutbound outbound;
  final AuthContext authContext;
  final Set<String> subGroupIds;

  private final MonoProcessor<Void> closeProcessor = MonoProcessor.create();
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

    if (authContext.isAnonymous()) {
      this.uid = sessionId;
    } else {
      this.uid = authContext.uid();
    }

    // 连接关闭
    this.inbound.withConnection(conn -> conn.onDispose().subscribe(closeProcessor));
    this.subGroupIds = new LinkedHashSet<>();
  }

  @Override
  public String sessionId() {
    return sessionId;
  }

  @Override
  public String uid() {
    return uid;
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
  public Set<String> subGroupIds() {
    return subGroupIds;
  }

  @Override
  public Mono<Void> sendPacket(Publisher<Packet> packet) {
    return Mono.from(packet)
        .map(
            p -> {
              try {
                var buf = outbound.alloc().buffer();
                OutputStream bbos = new ByteBufOutputStream(buf);
                PacketHelper.MAPPER.writeValue(bbos, p);
                return buf;
              } catch (IOException e) {
                log.error(
                    "序列化 Packet 失败 [sessionId={}, uid={}]\nPacket:\n{}",
                    this.sessionId,
                    this.uid(),
                    p,
                    e);
                throw new RuntimeException("序列化 Packet 失败", e);
              }
            })
        .flatMap(this::send)
        .then();
  }

  @Override
  public Mono<Void> send(Publisher<TextWebSocketFrame> frame) {
    return outbound.sendObject(frame).then();
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
    return closeProcessor;
  }

  @Override
  public String toString() {
    HttpServerRequest request = (HttpServerRequest) inbound;
    return new StringBuilder()
        .append("Session{")
        .append("sessionId=")
        .append(sessionId)
        .append(", ")
        .append("uid=")
        .append(uid)
        .append(", ")
        .append("clientIp=")
        .append(request.remoteAddress().getAddress().getHostAddress())
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
