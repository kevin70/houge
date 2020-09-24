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

import java.util.Set;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 群组会话管理.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface SessionGroupManager {

  /**
   * 注册会话群组监听器.
   *
   * @param sessionGroupListener 会话群组监听器
   * @return 如果返回 true 则表示注册成功
   */
  boolean registerListener(SessionGroupListener sessionGroupListener);

  /**
   * 取消会话群组监听器.
   *
   * @param sessionGroupListener 会话群组监听器
   * @return 如果返回 true 则表示取消成功
   */
  boolean unregisterListener(SessionGroupListener sessionGroupListener);

  /**
   * 会话订阅群组.
   *
   * @param groupIds 群组 IDs
   */
  Mono<Void> subGroups(Session session, Set<String> groupIds);

  /**
   * 会话取消订阅群组.
   *
   * @param groupIds 群组 IDs
   */
  Mono<Void> unsubGroups(Session session, Set<String> groupIds);

  /**
   * 查询群组下订阅的所有会话.
   *
   * @param groupId 群组 ID
   * @return 已订阅群组的会话
   */
  Flux<Session> findByGroupId(String groupId);
}
