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

import cool.houge.model.User;
import cool.houge.r2dbc.Parameter;
import cool.houge.r2dbc.R2dbcClient;
import cool.houge.storage.SqlStates;
import cool.houge.storage.UserDao;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.StacklessBizCodeException;

/**
 * 用户数据访问接口实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class UserDaoImpl implements UserDao {

  private static final Logger log = LogManager.getLogger();

  private static final String NEXT_ID_SQL = "SELECT NEXTVAL('users_id_seq')";
  private static final String INSERT_SQL =
      "INSERT INTO users(id,origin_uid,create_time,update_time) VALUES($1,$2,NOW(),NOW())";

  private final R2dbcClient rc;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public UserDaoImpl(R2dbcClient rc) {
    this.rc = rc;
  }

  @Override
  public Mono<Long> insert(User entity) {
    return Mono.justOrEmpty(entity.getId())
        .switchIfEmpty(nextUserId())
        .flatMap(
            id ->
                rc.sql(INSERT_SQL)
                    .bind(
                        new Object[] {
                          id, Parameter.fromOrNull(entity.getOriginUid(), String.class)
                        })
                    .rowsUpdated()
                    .thenReturn(id))
        .onErrorMap(
            R2dbcDataIntegrityViolationException.class,
            ex -> {
              log.debug("用户已存在 {} ~ {}", entity, ex.getMessage());
              if (SqlStates.S23505.equals(ex.getSqlState())) {
                return new StacklessBizCodeException(BizCode.C810, "用户已存在", ex);
              }
              return ex;
            });
  }

  private Mono<Long> nextUserId() {
    return Mono.defer(() -> rc.sql(NEXT_ID_SQL).map(row -> row.get(0, Long.class)).one());
  }
}
