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
package top.yein.tethys.storage;

import java.util.List;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.Message;

/**
 * 消息数据访问实体.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageDao {

  /**
   * 保存消息.
   *
   * @param entity 消息实体
   * @return 受影响行数
   */
  Mono<Integer> insert(Message entity);

  /**
   * 保存消息并与用户进行关联.
   *
   * @param entity 消息实体
   * @param uids 关联的用户 IDs
   * @return 受影响行数
   */
  Mono<Void> insert(Message entity, List<Long> uids);

  /**
   * 更新消息 {@code unread} 列的值.
   *
   * @param id 消息 ID
   * @param v {@code unread} 的值
   * @return 受影响行数
   */
  Mono<Integer> updateUnread(String id, int v);
}
