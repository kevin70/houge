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

import reactor.core.publisher.Mono;
import top.yein.tethys.entity.ServerInstance;

/**
 * 服务实例数据访问对象.
 *
 * @author KK (kzou227@qq.com)
 */
public interface ServerInstanceDao {

  /**
   * 新增应用服务实例.
   *
   * @param entity 实体
   * @return 受影响行数
   */
  Mono<Integer> insert(ServerInstance entity);

  /**
   * 删除应用服务实例.
   *
   * @param id 应用 ID
   * @return 受影响行数
   */
  Mono<Integer> delete(int id);

  /**
   * 更新应用服务实例.
   *
   * @param entity 实体
   * @return 受影响行数
   */
  Mono<Integer> update(ServerInstance entity);

  /**
   * 更新最后检查时间.
   *
   * @param id 应用 ID
   * @return 受影响行数
   */
  Mono<Integer> updateCheckTime(int id);

  /**
   * 查询应用服务实例.
   *
   * @param id 应用 ID
   * @return 实体
   */
  Mono<ServerInstance> findById(int id);
}
