package top.yein.tethys.storage;

import reactor.core.publisher.Mono;
import top.yein.tethys.entity.User;

/**
 * 用户数据访问接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface UserDao {

  /**
   * 保存用户信息.
   *
   * @param entity 用户实体
   * @return 用户 ID
   */
  Mono<Long> insert(User entity);
}
