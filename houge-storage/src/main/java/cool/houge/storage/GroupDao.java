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

import cool.houge.model.Group;
import reactor.core.publisher.Mono;

/**
 * 群组数据访问接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface GroupDao {

  /**
   * 保存群组信息.
   *
   * @param entity 群实体
   * @return 群组 ID
   */
  Mono<Long> insert(Group entity);

  /**
   * 删除群组.
   *
   * @param gid 群组 ID
   * @return RS
   */
  Mono<Void> delete(long gid);

  /**
   * 在指定的群中将指定用户设置为群成员.
   *
   * @param gid 群组 ID
   * @param uid 用户 ID
   * @return RS
   */
  Mono<Void> joinMember(long gid, long uid);

  /**
   * 将指定的群组中的指定用户移除.
   *
   * @param gid 群组 ID
   * @param uid 用户 ID
   * @return RS
   */
  Mono<Void> removeMember(long gid, long uid);
}
