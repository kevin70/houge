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
import top.yein.tethys.entity.UidMapping;

/**
 * {@code uid_mappings} 表数据访问类.
 *
 * @author KK (kzou227@qq.com)
 */
public interface UidMappingDao {

  /**
   * 保存用户 ID 映射关系.
   *
   * @param entity 实体
   * @return 用户 ID
   */
  Mono<Long> insert(UidMapping entity);

  /**
   * 根据被映射的用户 ID 查询用户映射实体.
   *
   * @param mappedUid 被映射的用户 ID
   * @return 映射实体
   */
  Mono<UidMapping> findByMappedUid(String mappedUid);
}
