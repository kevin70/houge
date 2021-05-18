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
package cool.houge.storage;

import cool.houge.storage.entity.Message;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * 消息数据访问实体.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageDao {

  /**
   * 保存消息并与用户进行关联.
   *
   * @param entity 消息实体
   * @param uids 关联的用户 IDs
   * @return 受影响行数
   */
  Mono<Void> insert(Message entity, List<Long> uids);

  /**
   * 批量将用户的消息 {@code unread} 修改为指定状态.
   *
   * @param uid 用户 ID
   * @param messageIds 消息 IDs
   * @param v 状态
   * @return RS
   */
  Mono<Void> updateUnreadStatus(long uid, List<String> messageIds, int v);
}
