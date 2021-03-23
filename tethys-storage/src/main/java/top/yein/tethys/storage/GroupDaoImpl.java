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

import com.google.common.annotations.VisibleForTesting;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.Group;
import top.yein.tethys.r2dbc.Parameter;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * 群组数据访问实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupDaoImpl implements GroupDao {

  private static final String NEXT_GID_SQL = "select nextval('groups_id_seq')";
  private static final String INSERT_GROUP_SQL =
      "INSERT INTO groups(id,name,creator_id,owner_id,member_size,member_limit,create_time,update_time)"
          + " VALUES($1,$2,$3,$4,$5,$6,now(),now())";
  private static final String INC_MEMBER_SIZE_SQL =
      "UPDATE GROUPS SET member_size=member_size+$1 WHERE id=$2 AND member_limit>=member_size+$1";
  private static final String DEC_MEMBER_SIZE_SQL =
      "UPDATE GROUPS SET member_size=member_size-$1 WHERE id=$2 AND member_size>=$1";
  private static final String INSERT_MEMBER_SQL =
      "INSERT INTO groups_member(gid,uid,create_time) VALUES($1,$2,now())";

  private final R2dbcClient rc;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public GroupDaoImpl(R2dbcClient rc) {
    this.rc = rc;
  }

  @Override
  public Mono<Long> insert(Group entity) {
    return nextGroupId()
        .delayUntil(
            id -> {
              // 保存群组信息
              var m1 =
                  rc.sql(INSERT_GROUP_SQL)
                      .bind(
                          new Object[] {
                            id,
                            Parameter.fromOrNull(entity.getName(), String.class),
                            entity.getCreatorId(),
                            entity.getOwnerId(),
                            entity.getMemberSize(),
                            entity.getMemberLimit()
                          })
                      .rowsUpdated();

              // 保存群组成员关系
              var m2 =
                  rc.sql(INSERT_MEMBER_SQL)
                      .bind(new Object[] {id, entity.getCreatorId()})
                      .rowsUpdated();
              return Mono.zip(m1, m2);
            });
  }

  @Override
  public Mono<Integer> joinMember(long gid, long uid) {
    return null;
  }

  @Override
  public Mono<Integer> removeMember(long gid, long uid) {
    return null;
  }

  @VisibleForTesting
  Mono<Integer> incMemberSize(long id, int size) {
    return rc.sql(INC_MEMBER_SIZE_SQL).bind(0, size).bind(1, id).rowsUpdated();
  }

  @VisibleForTesting
  Mono<Integer> decMemberSize(long id, int size) {
    return rc.sql(DEC_MEMBER_SIZE_SQL).bind(0, size).bind(1, id).rowsUpdated();
  }

  private Mono<Long> nextGroupId() {
    return rc.sql(NEXT_GID_SQL).map(row -> row.get(0, Long.class)).one();
  }
}
