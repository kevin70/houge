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

import com.google.common.annotations.VisibleForTesting;
import cool.houge.model.Group;
import cool.houge.r2dbc.R2dbcClient;
import cool.houge.storage.GroupDao;
import cool.houge.storage.SqlStates;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.chaos.biz.StacklessBizCodeException;

/**
 * 群组数据访问实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupDaoImpl implements GroupDao {

  private static final Logger log = LogManager.getLogger();

  private static final String NEXT_GID_SQL = "select nextval('groups_id_seq')";
  private static final String INSERT_GROUP_SQL =
      "INSERT INTO groups(id,creator_id,owner_id,member_size,create_time,update_time)"
          + " VALUES($1,$2,$3,$4,now(),now())";
  private static final String DELETE_GROUP_SQL = "DELETE FROM groups WHERE id=$1";
  private static final String DELETE_MEMBERS_SQL = "DELETE FROM groups_member WHERE gid=$1";
  private static final String INC_MEMBER_SIZE_SQL =
      "UPDATE GROUPS SET member_size=member_size+$1 WHERE id=$2";
  private static final String DEC_MEMBER_SIZE_SQL =
      "UPDATE GROUPS SET member_size=member_size-$1 WHERE id=$2";
  private static final String INSERT_MEMBER_SQL =
      "INSERT INTO groups_member(gid,uid,create_time) VALUES($1,$2,now())";
  private static final String DELETE_MEMBER_SQL =
      "DELETE FROM groups_member WHERE gid=$1 AND uid=$2";

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
    return Mono.justOrEmpty(entity.getId())
        .switchIfEmpty(nextGroupId())
        .flatMap(
            id -> {
              // 保存群组信息
              var m1 =
                  rc.sql(INSERT_GROUP_SQL)
                      .bind(
                          new Object[] {
                            id, entity.getCreatorId(), entity.getOwnerId(), entity.getMemberSize()
                          })
                      .rowsUpdated();

              // 保存群组成员关系
              var m2 =
                  rc.sql(INSERT_MEMBER_SQL)
                      .bind(new Object[] {id, entity.getCreatorId()})
                      .rowsUpdated();
              return Mono.zip(m1, m2).thenReturn(id);
            })
        .onErrorMap(
            R2dbcDataIntegrityViolationException.class,
            ex -> {
              log.debug("群组已存在 {} ~ {}", entity, ex.getMessage());
              if (SqlStates.S23505.equals(ex.getSqlState())) {
                return new StacklessBizCodeException(BizCode.C810, "群组已存在", ex);
              }
              return ex;
            });
  }

  @Override
  public Mono<Void> delete(long gid) {
    var m1 = rc.sql(DELETE_GROUP_SQL).bind(0, gid).rowsUpdated();
    var m2 = rc.sql(DELETE_MEMBERS_SQL).bind(0, gid).rowsUpdated();
    return m1.zipWith(m2).then();
  }

  @Override
  public Mono<Void> joinMember(long gid, long uid) {
    // 新增群组-用户关系映射
    var m1 =
        rc.sql(INSERT_MEMBER_SQL)
            .bind(new Object[] {gid, uid})
            .rowsUpdated()
            .onErrorMap(
                R2dbcDataIntegrityViolationException.class,
                ex -> {
                  log.debug("已是群成员 gid={} uid={} ~ {}", gid, uid, ex.getMessage());
                  if (SqlStates.S23505.equals(ex.getSqlState())) {
                    return new StacklessBizCodeException(BizCode.C810, "已是群成员", ex)
                        .addContextValue("gid", gid)
                        .addContextValue("uid", uid);
                  }
                  return ex;
                })
            .doOnNext(
                n -> {
                  if (n != 1) {
                    throw new BizCodeException(BizCode.C811, "joinMember 增加群成员关系异常")
                        .addContextValue("gid", gid)
                        .addContextValue("uid", uid);
                  }
                });

    // 增加成员数量
    var m2 =
        incMemberSize(gid, 1)
            .doOnNext(
                n -> {
                  if (n != 1) {
                    throw new BizCodeException(BizCode.C811, "joinMember 增加群成员数量异常")
                        .addContextValue("gid", gid)
                        .addContextValue("uid", uid);
                  }
                });
    return Mono.zip(m1, m2).then();
  }

  @Override
  public Mono<Void> removeMember(long gid, long uid) {
    // 移除群组-用户关系映射
    var m1 =
        rc.sql(DELETE_MEMBER_SQL)
            .bind(new Object[] {gid, uid})
            .rowsUpdated()
            .doOnNext(
                n -> {
                  if (n != 1) {
                    throw new BizCodeException(BizCode.C811, "removeMember 移除群成员关系异常")
                        .addContextValue("gid", gid)
                        .addContextValue("uid", uid);
                  }
                });
    // 减少成员数量
    var m2 =
        decMemberSize(gid, 1)
            .doOnNext(
                n -> {
                  if (n != 1) {
                    throw new BizCodeException(BizCode.C811, "removeMember 减少群成员数量异常")
                        .addContextValue("gid", gid)
                        .addContextValue("uid", uid);
                  }
                });
    return Mono.zip(m1, m2).then();
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
    return Mono.defer(() -> rc.sql(NEXT_GID_SQL).map(row -> row.get(0, Long.class)).one());
  }
}
