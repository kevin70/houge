package top.yein.tethys.storage;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.User;
import top.yein.tethys.r2dbc.Parameter;
import top.yein.tethys.r2dbc.R2dbcClient;

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
