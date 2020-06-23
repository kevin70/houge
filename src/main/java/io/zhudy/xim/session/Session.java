package io.zhudy.xim.session;

import io.netty.buffer.ByteBuf;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.packet.Packet;
import java.util.Collections;
import java.util.Set;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * IM 会话信息.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface Session {

  /**
   * 唯一会话 ID.
   *
   * <p>实现需要保证会话 ID 在当前应用中的唯一.
   *
   * @return
   */
  long sessionId();

  /**
   * 返回认证用户 ID.
   *
   * <p>如果为匿名认证则返回默认生成的临时用户 ID.
   *
   * @return 用户 ID
   */
  String uid();

  /**
   * 会话认证的上下文信息. 未认证则返回 {@link AuthContext#NONE_AUTH_CONTEXT}.
   *
   * @return 会话认证的上下文信息
   */
  default AuthContext authContext() {
    return AuthContext.NONE_AUTH_CONTEXT;
  }

  /**
   * 是否为匿名认证.
   *
   * @return true 是匿名认证
   */
  default boolean isAnonymous() {
    return authContext().isAnonymous();
  }

  /**
   * 会话是否关闭.
   *
   * @return true 会话已关闭
   */
  default boolean isClosed() {
    return true;
  }

  /**
   * 已订阅群组消息的 IDs.
   *
   * @return 订阅群组 IDs
   */
  default Set<String> subGroupIds() {
    return Collections.emptySet();
  }

  /**
   * 向客户端发送数据.
   *
   * @param packet 数据包
   * @return Mono
   */
  default Mono<Void> sendPacket(Packet packet) {
    return sendPacket(Mono.just(packet));
  }

  /**
   * 向客户端发送数据.
   *
   * @param packet 数据包
   * @return Mono
   */
  default Mono<Void> sendPacket(Publisher<Packet> packet) {
    return Mono.empty();
  }

  /**
   * 向客户端发送数据.
   *
   * @param buf 数据
   * @return Mono
   */
  default Mono<Void> send(ByteBuf buf) {
    return send(Mono.just(buf));
  }

  /**
   * 向客户端发送数据.
   *
   * @param buf 数据
   * @return Mono
   */
  default Mono<Void> send(Publisher<ByteBuf> buf) {
    return Mono.empty();
  }

  /** 关闭会话. */
  default Mono<Void> close() {
    return Mono.empty();
  }

  /** 关闭会话的事件回调. */
  default Mono<Void> onClose() {
    return Mono.empty();
  }
}
