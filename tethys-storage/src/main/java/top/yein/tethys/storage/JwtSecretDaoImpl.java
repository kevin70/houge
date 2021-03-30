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

import com.auth0.jwt.algorithms.Algorithm;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.annotations.VisibleForTesting;
import io.r2dbc.spi.Row;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.domain.CachedJwtAlgorithm;
import top.yein.tethys.entity.JwtSecret;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * JWT 密钥存储 - PostgreSQL.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class JwtSecretDaoImpl implements JwtSecretDao {

  private static final String INSERT_SQL =
      "INSERT INTO jwt_secrets(id,algorithm,secret_key) VALUES($1,$2,$3)";
  private static final String DELETE_SQL = "UPDATE jwt_secrets SET deleted=$1 WHERE id=$2";
  private static final String FIND_BY_ID_SQL = "SELECT * FROM jwt_secrets WHERE id=$1";
  private static final String FIND_ALL_SQL = "SELECT * FROM jwt_secrets";

  private final R2dbcClient rc;
  private final AsyncCache<String, CachedJwtAlgorithm> jwtAlgorithmCache;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public JwtSecretDaoImpl(R2dbcClient rc) {
    this.rc = rc;
    this.jwtAlgorithmCache =
        Caffeine.newBuilder()
            .recordStats()
            .refreshAfterWrite(REFRESH_CACHE_DURATION)
            .maximumSize(Byte.MAX_VALUE)
            .buildAsync(cacheMappingFunc(Context.empty())::apply);
  }

  @Override
  public Mono<Void> insert(JwtSecret entity) {
    return rc.sql(INSERT_SQL)
        .bind(new Object[] {entity.getId(), entity.getAlgorithm(), entity.getSecretKey()})
        .rowsUpdated()
        .doOnNext(
            n -> {
              if (n != 1) {
                throw new BizCodeException(BizCode.C811, "保存 jwt secret 失败")
                    .addContextValue("entity", entity);
              }
            })
        .then();
  }

  @Override
  public Mono<Void> delete(String id) {
    return rc.sql(DELETE_SQL)
        .bind(0, System.currentTimeMillis() / 1000)
        .bind(1, id)
        .rowsUpdated()
        .doOnNext(
            n -> {
              if (n != 1) {
                throw new BizCodeException(BizCode.C811, "删除 jwt secret 失败")
                    .addContextValue("id", id);
              }
            })
        .then()
        .doOnSuccess(unused -> jwtAlgorithmCache.synchronous().invalidate(id));
  }

  @Override
  public Mono<JwtSecret> findById(String id) {
    return rc.sql(FIND_BY_ID_SQL).bind(0, id).map(this::mapEntity).one();
  }

  @Override
  public Flux<JwtSecret> findAll() {
    return rc.sql(FIND_ALL_SQL).map(this::mapEntity).all();
  }

  @Override
  public Flux<CachedJwtAlgorithm> refreshAll() {
    return findAll()
        .flatMap(
            jwtSecret -> {
              var jwtAlgorithm = toCachedJwtAlgorithm(jwtSecret);
              jwtAlgorithmCache.put(
                  jwtSecret.getId(), CompletableFuture.completedFuture(jwtAlgorithm));
              return Mono.just(jwtAlgorithm);
            });
  }

  @Override
  public Mono<CachedJwtAlgorithm> loadById(String id) {
    return Mono.deferContextual(
        context -> Mono.fromFuture(jwtAlgorithmCache.get(id, cacheMappingFunc(context))));
  }

  @Override
  public Flux<CachedJwtAlgorithm> loadNoDeleted() {
    var values = jwtAlgorithmCache.asMap().values();
    var publishers = new ArrayList<Mono<CachedJwtAlgorithm>>(values.size());
    for (CompletableFuture<CachedJwtAlgorithm> value : values) {
      publishers.add(Mono.fromFuture(value));
    }
    Predicate<CachedJwtAlgorithm> filterDeleted =
        cachedJwtAlgorithm -> cachedJwtAlgorithm.getDeleted() == 0;
    return Flux.concat(publishers)
        .filter(filterDeleted)
        .switchIfEmpty(refreshAll().filter(filterDeleted));
  }

  private JwtSecret mapEntity(Row row) {
    var e = new JwtSecret();
    e.setId(row.get("id", String.class));
    e.setAlgorithm(row.get("algorithm", String.class));
    e.setSecretKey(row.get("secret_key", ByteBuffer.class));
    e.setDeleted(row.get("deleted", Integer.class));
    e.setCreateTime(row.get("create_time", LocalDateTime.class));
    e.setUpdateTime(row.get("update_time", LocalDateTime.class));
    return e;
  }

  private BiFunction<String, Executor, CompletableFuture<CachedJwtAlgorithm>> cacheMappingFunc(
      ContextView context) {
    return (s, executor) -> {
      log.debug("加载 jwt secret [kid={}]", s);
      return findById(s)
          .map(this::toCachedJwtAlgorithm)
          // context 用于事务传递
          .contextWrite(context)
          .toFuture();
    };
  }

  @VisibleForTesting
  CachedJwtAlgorithm toCachedJwtAlgorithm(JwtSecret e) {
    var name = e.getAlgorithm();
    Algorithm algorithm;
    if ("HS256".equals(name)) {
      algorithm = Algorithm.HMAC256(e.getSecretKey().array());
    } else if ("HS512".equals(name)) {
      algorithm = Algorithm.HMAC512(e.getSecretKey().array());
    } else {
      throw new BizCodeException(BizCodes.C3311).addContextValue("algorithm", name);
    }
    return CachedJwtAlgorithm.builder()
        .id(e.getId())
        .algorithm(algorithm)
        .deleted(e.getDeleted())
        .build();
  }
}
