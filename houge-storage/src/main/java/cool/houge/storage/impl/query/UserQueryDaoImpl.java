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
package cool.houge.storage.impl.query;

import cool.houge.Nil;
import cool.houge.r2dbc.R2dbcClient;
import cool.houge.model.User;
import cool.houge.storage.query.UserQueryDao;
import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * 用户查询数据访问接口实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class UserQueryDaoImpl implements UserQueryDao {

  private static final String QUERY_BY_ID_SQL = "SELECT * FROM users WHERE id=$1";
  private static final String EXISTS_BY_ID_SQL = "SELECT COUNT(*) FROM users WHERE id=$1";

  private final R2dbcClient rc;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public UserQueryDaoImpl(R2dbcClient rc) {
    this.rc = rc;
  }

  @Override
  public Mono<User> queryById(long id) {
    return rc.sql(QUERY_BY_ID_SQL).bind(0, id).map(this::mapToEntity).one();
  }

  @Override
  public Mono<Nil> existsById(long id) {
    return rc.sql(EXISTS_BY_ID_SQL)
        .bind(0, id)
        .map(row -> row.get(0, Integer.class))
        .one()
        .flatMap(count -> Objects.equals(count, 1) ? Nil.mono() : Mono.empty());
  }

  private User mapToEntity(Row row) {
    var e = new User();
    e.setId(row.get("id", Long.class));
    e.setOriginUid(row.get("origin_uid", String.class));
    e.setCreateTime(row.get("create_time", LocalDateTime.class));
    e.setUpdateTime(row.get("update_time", LocalDateTime.class));
    return e;
  }
}
