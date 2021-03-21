package top.yein.tethys.storage.query;

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

  private final static String QUERY_BY_ID_SQL = "SELECT * FROM users WHERE id=$1";
  private final static String EXISTS_BY_ID_SQL = "SELECT COUNT(*) FROM users WHERE id=$1";

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
    return null;
  }

  @Override
  public Mono<Boolean> existsById(long id) {
    return null;
  }
}
