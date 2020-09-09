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
package io.zhudy.xim.session;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 会话管理器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface SessionManager {

  /** */
  String SESSION_LISTENER_NAME_FOR_IOC = "session.listeners";

  /**
   * 添加会话.
   *
   * @param session 会话
   */
  Mono<Void> add(Session session);

  /**
   * 移除会话.
   *
   * @param session 会话
   */
  Mono<Void> remove(Session session);

  /**
   * 根据会话 ID 移除会话.
   *
   * @param sessionId 会话 ID
   * @return 已经移除的会话
   */
  Mono<Session> removeById(String sessionId);

  /**
   * 根据用户认证 ID 移除会话.
   *
   * @param uid 用户认证 ID
   * @return 已经移除的会话
   */
  Flux<Session> removeByUid(String uid);

  /**
   * 根据会话 ID 查询会话.
   *
   * @param sessionId 会话 ID
   * @return 会话
   */
  Mono<Session> findById(String sessionId);

  /**
   * 根据用户认证 ID 查询会话.
   *
   * @param uid 用户认证 ID
   * @return 用户认证 ID 会话
   */
  Flux<Session> findByUid(String uid);

  /**
   * 返回当前所有会话.
   *
   * @return 当前所有会话.
   */
  Flux<Session> all();
}
