package top.yein.tethys.storage;

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

  private Mono<Long> nextGroupId() {
    return rc.sql(NEXT_GID_SQL).map(row -> row.get(0, Long.class)).one();
  }
}
