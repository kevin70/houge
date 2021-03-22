package top.yein.tethys.service;

import javax.inject.Inject;
import org.roaringbitmap.longlong.Roaring64NavigableMap;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.yein.tethys.Nil;
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

  /** 已存在的用户 ID 位图. */
  private Roaring64NavigableMap existingUidBits = Roaring64NavigableMap.bitmapOf();

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
  public Mono<Nil> existsById(long uid) {
    if (existingUidBits.contains(uid)) {
      return Nil.mono();
    }

    // 这里需要优化
    // 1. 优先查询 BitSet 判断是否是否存在
    // 2. BitSet 不存在查询 Redis 判断用户是否存在
    // 3. 以上都不存在时查询数据库
    return userQueryDao
        .existsById(uid)
        .flatMap(
            b -> {
              if (b) {
                return Nil.mono()
                    .publishOn(Schedulers.single())
                    .delayUntil(
                        unused -> {
                          // existingUidBits 是非线程安全的对象
                          // 将对 existingUidBits 所有的操作放置在同一个线程中避免额外的 Lock
                          existingUidBits.addLong(uid);
                          return Mono.empty();
                        })
                    .publishOn(Schedulers.parallel());
              }
              return Mono.empty();
            });
  }
}
