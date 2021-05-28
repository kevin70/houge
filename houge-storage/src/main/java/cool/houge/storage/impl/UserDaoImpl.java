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

import cool.houge.r2dbc.Parameter;
import cool.houge.r2dbc.R2dbcClient;
import cool.houge.model.User;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import cool.houge.storage.UserDao;

/**
 * 用户数据访问接口实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class UserDaoImpl implements UserDao {

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
                    .thenReturn(id));
  }

  private Mono<Long> nextUserId() {
    return rc.sql(NEXT_ID_SQL).map(row -> row.get(0, Long.class)).one();
  }
}
