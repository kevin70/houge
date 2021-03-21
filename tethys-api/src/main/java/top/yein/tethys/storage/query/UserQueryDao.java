package top.yein.tethys.storage.query;

import reactor.core.publisher.Mono;
import top.yein.tethys.entity.User;

/**
 * 用户查询数据访问接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface UserQueryDao {

  /**
   * 使用用户 ID 查询用户信息.
   *
   * @param id 用户 ID
   * @return 用户信息
   */
  Mono<User> queryById(long id);

  /**
   * 使用用户 ID 查询用户是否存在.
   *
   * @param id 用户 ID
   * @return true/false
   */
  Mono<Boolean> existsById(long id);
}
