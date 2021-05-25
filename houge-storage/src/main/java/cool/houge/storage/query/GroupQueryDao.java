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
package cool.houge.storage.query;

import cool.houge.Nil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import cool.houge.entity.Group;

/**
 * 群组查询数据访问接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface GroupQueryDao {

  /**
   * 查询指定群信息.
   *
   * @param id 群 ID
   * @return 群信息
   */
  Mono<Group> queryById(long id);

  /**
   * 查询指定群成员的用户 ID 列表.
   *
   * @param id 群 ID
   * @return 成员用户 ID
   */
  Flux<Long> queryUidByGid(long id);

  /**
   * 查询指定用户已经关联的群组 IDs.
   *
   * @param uid 用户 ID
   * @return 群组 IDs
   */
  Flux<Long> queryGidByUid(long uid);

  /**
   * 使用群组 ID 查询用户是否存在.
   *
   * @param id 群组 ID
   * @return Nil.mono()/Mono.empty()
   */
  Mono<Nil> existsById(long id);
}
