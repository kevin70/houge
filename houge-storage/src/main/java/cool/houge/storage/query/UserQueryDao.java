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
import reactor.core.publisher.Mono;
import cool.houge.entity.User;

/**
 * 用户查询数据访问接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface UserQueryDao {

  /**
   * 使用用户 ID 查询用户信息.
   *
   * @param id 用户 ID
   * @return 用户信息
   */
  Mono<User> queryById(long id);

  /**
   * 使用用户 ID 查询用户是否存在.
   *
   * @param id 用户 ID
   * @return true/false
   */
  Mono<Nil> existsById(long id);
}
