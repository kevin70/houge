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
package top.yein.tethys.storage.impl.query;

import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.inject.Inject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.Nil;
import top.yein.tethys.storage.entity.Group;
import top.yein.tethys.r2dbc.R2dbcClient;
import top.yein.tethys.storage.query.GroupQueryDao;

/**
 * 群组查询数据访问实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupQueryDaoImpl implements GroupQueryDao {

  private static final String QUERY_BY_ID_SQL = "SELECT * FROM groups WHERE id=$1";
  private static final String QUERY_MEMBERS_UID_SQL = "SELECT uid FROM groups_member WHERE gid=$1";
  private static final String QUERY_GID_BY_UID = "SELECT gid FROM groups_member WHERE uid=$1";
  private static final String EXISTS_BY_ID_SQL = "SELECT COUNT(*) FROM groups WHERE id=$1";

  private final R2dbcClient rc;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public GroupQueryDaoImpl(R2dbcClient rc) {
    this.rc = rc;
  }

  @Override
  public Mono<Group> queryById(long id) {
    return rc.sql(QUERY_BY_ID_SQL).bind(0, id).map(this::mapToGroup).one();
  }

  @Override
  public Flux<Long> queryUidByGid(long id) {
    return rc.sql(QUERY_MEMBERS_UID_SQL).bind(0, id).map(row -> row.get(0, Long.class)).all();
  }

  @Override
  public Flux<Long> queryGidByUid(long uid) {
    return rc.sql(QUERY_GID_BY_UID).bind(0, uid).map(row -> row.get(0, Long.class)).all();
  }

  @Override
  public Mono<Nil> existsById(long id) {
    return rc.sql(EXISTS_BY_ID_SQL)
        .bind(0, id)
        .map(row -> row.get(0, Integer.class))
        .one()
        .flatMap(count -> Objects.equals(count, 1) ? Nil.mono() : Mono.empty());
  }

  private Group mapToGroup(Row row) {
    var e = new Group();
    e.setId(row.get("id", Long.class));
    e.setCreatorId(row.get("creator_id", Long.class));
    e.setOwnerId(row.get("owner_id", Long.class));
    e.setMemberSize(row.get("member_size", Integer.class));
    e.setCreateTime(row.get("create_time", LocalDateTime.class));
    e.setUpdateTime(row.get("update_time", LocalDateTime.class));
    return e;
  }
}
