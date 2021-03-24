package top.yein.tethys.service;

import reactor.core.publisher.Mono;
import top.yein.tethys.Nil;

/**
 * 用户服务接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface UserService {

  /**
   * 判断指定用户是否存在.
   *
   * <p>如果用户存在则返回一个 {@code Mono<Null>} 实例可用 {@code Mono} 操作符进行消费, 反之则返回 {@code Mono.empty()}.
   *
   * @param uid 用户 ID
   * @return true/false
   */
  Mono<Nil> existsById(long uid);
}
