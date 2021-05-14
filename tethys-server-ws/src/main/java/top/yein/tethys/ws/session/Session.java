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
package top.yein.tethys.ws.session;

import io.netty.buffer.ByteBuf;
import java.util.Set;
import reactor.core.publisher.Mono;

/**
 * 会话信息.
 *
 * @author KK (kzou227@qq.com)
 */
public interface Session {

  /**
   * 返回会话ID应用内唯一.
   *
   * @return 会话ID
   */
  long sessionId();

  /**
   * 返回认证用户ID.
   *
   * @return 用户ID
   */
  long uid();

  /**
   * 返回户认证的令牌.
   *
   * @return 令牌
   */
  String token();

  /**
   * 已订阅群组消息的 IDs.
   *
   * @return 订阅群组 IDs
   */
  Set<Long> subGroupIds();

  /**
   * 会话是否关闭.
   *
   * @return true 会话已关闭
   */
  boolean isClosed();

  /**
   * 向客户端发送数据.
   *
   * @param source 数据
   * @return Mono
   */
  Mono<Void> send(Mono<ByteBuf> source);

  /** 关闭会话. */
  Mono<Void> close();
}
