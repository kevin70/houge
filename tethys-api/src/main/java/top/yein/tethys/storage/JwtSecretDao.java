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

import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.domain.CachedJwtAlgorithm;
import top.yein.tethys.storage.entity.JwtSecret;

/**
 * JWT 密钥存储.
 *
 * @author KK (kzou227@qq.com)
 */
public interface JwtSecretDao {

  /** 缓存默认刷新周期. */
  Duration REFRESH_CACHE_DURATION = Duration.ofMinutes(5);

  /**
   * 保存数据并返回受影响行记录数.
   *
   * @param entity 实体
   * @return 受影响行记录数
   */
  Mono<Void> insert(JwtSecret entity);

  /**
   * 软删除 JWT 密钥.
   *
   * @param id JWT kid
   * @return 受影响行记录数
   */
  Mono<Void> delete(String id);

  /**
   * 查询数据并返回实体.
   *
   * @param id JWT kid
   * @return 实体
   */
  Mono<JwtSecret> findById(String id);

  /**
   * 查询所有的 JWT 密钥配置.
   *
   * @return 实体
   */
  Flux<JwtSecret> findAll();

  /**
   * 查询所有 JWT 密钥配置并刷新缓存.
   *
   * @return 缓存实体
   */
  Flux<CachedJwtAlgorithm> refreshAll();

  /**
   * 优先从缓存中查询数据并返回缓存实体.
   *
   * @param id JWT kid
   * @return 缓存实体
   */
  Mono<CachedJwtAlgorithm> loadById(String id);

  /**
   * 加载未被软删除的缓存实体.
   *
   * @return 缓存实体
   */
  Flux<CachedJwtAlgorithm> loadNoDeleted();
}
