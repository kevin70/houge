package top.yein.tethys.storage;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.Group;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * 群组数据访问实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupDaoImpl implements GroupDao {

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
  public Mono<Integer> insert(Group entity) {
    return null;
  }

  @Override
  public Mono<Integer> joinMember(long gid, long uid) {
    return null;
  }

  @Override
  public Mono<Integer> removeMember(long gid, long uid) {
    return null;
  }
}
