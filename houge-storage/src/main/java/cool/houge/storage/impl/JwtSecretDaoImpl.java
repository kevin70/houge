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
package cool.houge.storage.impl;

import cool.houge.r2dbc.R2dbcClient;
import cool.houge.entity.JwtSecret;
import io.r2dbc.spi.Row;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import cool.houge.storage.JwtSecretDao;

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

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public JwtSecretDaoImpl(R2dbcClient rc) {
    this.rc = rc;
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
        .then();
  }

  @Override
  public Mono<JwtSecret> findById(String id) {
    return rc.sql(FIND_BY_ID_SQL).bind(0, id).map(this::mapEntity).one();
  }

  @Override
  public Flux<JwtSecret> findAll() {
    return rc.sql(FIND_ALL_SQL).map(this::mapEntity).all();
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
}
