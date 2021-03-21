package top.yein.tethys.storage.query;

import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.User;
import top.yein.tethys.r2dbc.R2dbcClient;

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
  public Mono<Boolean> existsById(long id) {
    return rc.sql(EXISTS_BY_ID_SQL)
        .bind(0, id)
        .map(row -> Objects.equals(row.get(0, Integer.class), 1) ? true : false)
        .one();
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
