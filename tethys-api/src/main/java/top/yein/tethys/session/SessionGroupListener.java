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

import reactor.core.publisher.Mono;

/**
 * Session 事件接口.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@FunctionalInterface
public interface SessionGroupListener {

  /**
   * 事件处理.
   *
   * @param session 会话信息
   * @param event 事件类型
   * @param groupId 分组 ID
   * @return {@link Mono#empty()}
   */
  Mono<Void> handle(Session session, SessionGroupEvent event, String groupId);
}
