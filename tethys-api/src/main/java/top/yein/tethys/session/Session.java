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
package top.yein.tethys.session;

import io.netty.buffer.ByteBuf;
import java.util.Set;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import top.yein.tethys.auth.AuthContext;
import top.yein.tethys.packet.Packet;

/**
 * IM 会话信息.
 *
 * @author KK (kzou227@qq.com)
 */
public interface Session {

  /**
   * 唯一会话 ID.
   *
   * <p>实现需要保证会话 ID 在当前应用中的唯一.
   *
   * @return 会话 ID
   */
  String sessionId();

  /**
   * 会话认证的上下文信息.
   *
   * @return 会话认证的上下文信息
   */
  AuthContext authContext();

  /**
   * 返回认证用户 ID.
   *
   * <p>如果为匿名认证则返回默认生成的临时用户 ID.
   *
   * @return 用户 ID
   */
  default long uid() {
    return authContext().uid();
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
  boolean isClosed();

  /**
   * 已订阅群组消息的 IDs.
   *
   * @return 订阅群组 IDs
   */
  Set<Long> subGroupIds();

  /**
   * 向客户端发送数据.
   *
   * @param packet 数据包
   * @return RS
   */
  default Mono<Void> sendPacket(Packet packet) {
    return Mono.just(packet).transform(this::sendPacket);
  }

  /**
   * 向客户端发送数据.
   *
   * @param source 数据包
   * @return Mono
   */
  default Mono<Void> sendPacket(Publisher<Packet> source) {
    return Mono.empty();
  }

  /**
   * 向客户端发送数据.
   *
   * @param source 数据
   * @return Mono
   */
  Mono<Void> send(Publisher<ByteBuf> source);

  /** 关闭会话. */
  Mono<Void> close();

  /** 关闭会话的事件回调. */
  Mono<Void> onClose();
}
