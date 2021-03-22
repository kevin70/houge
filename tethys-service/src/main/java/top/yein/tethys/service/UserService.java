package top.yein.tethys.service;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.Null;
import top.yein.tethys.storage.UserDao;
import top.yein.tethys.storage.query.UserQueryDao;

/**
 * 用户服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class UserService {

  private final UserDao userDao;
  private final UserQueryDao userQueryDao;

  /**
   * @param userDao
   * @param userQueryDao
   */
  @Inject
  public UserService(UserDao userDao, UserQueryDao userQueryDao) {
    this.userDao = userDao;
    this.userQueryDao = userQueryDao;
  }

  /**
   * 判断指定用户是否存在.
   *
   * <p>如果用户存在则返回一个 {@code Mono<Null>} 实例可用 {@code Mono} 操作符进行消费, 反之则返回 {@code Mono.empty()}.
   *
   * @param uid 用户 ID
   * @return true/false
   */
  public Mono<Null> existsById(long uid) {
    // 这里需要优化
    // 1. 优先查询 BitSet 判断是否是否存在
    // 2. BitSet 不存在查询 Redis 判断用户是否存在
    // 3. 以上都不存在时查询数据库
    return userQueryDao.existsById(uid).flatMap(b -> b ? Null.toMono() : Mono.empty());
  }
}
