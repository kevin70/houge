package io.zhudy.xim.session.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.helper.PacketHelper;
import io.zhudy.xim.session.Session;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

/**
 * 默认会话实现.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public final class DefaultSession implements Session {

  final long sessionId;
  final String uid;
  final WebsocketInbound inbound;
  final WebsocketOutbound outbound;
  final AuthContext authContext;

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
      long sessionId,
      WebsocketInbound inbound,
      WebsocketOutbound outbound,
      AuthContext authContext) {
    this.sessionId = sessionId;
    this.inbound = inbound;
    this.outbound = outbound;
    this.authContext = authContext;

    if (authContext.isAnonymous()) {
      this.uid = Long.toHexString(this.sessionId);
    } else {
      this.uid = authContext.uid();
    }

    // 连接关闭
    this.inbound.withConnection(conn -> conn.onDispose().subscribe(closeProcessor));
  }

  @Override
  public long sessionId() {
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
  public Mono<Void> sendPacket(Publisher<Packet> packet) {
    return Flux.from(packet)
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
        .transform(this::send)
        .then();
  }

  @Override
  public Mono<Void> send(Publisher<ByteBuf> buf) {
    return outbound.send(buf).then();
  }

  @Override
  public Mono<Void> close() {
    if (closed.compareAndSet(false, true)) {
      // 触发 onClose 事件
      return outbound.sendClose();
    }
    return Mono.empty();
  }

  @Override
  public Mono<Void> onClose() {
    return closeProcessor;
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
