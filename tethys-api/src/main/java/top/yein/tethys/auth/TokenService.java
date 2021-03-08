package top.yein.tethys.auth;

import reactor.core.publisher.Mono;

/**
 * 令牌服务.
 *
 * @author KK (kzou227@qq.com)
 */
public interface TokenService {

  /**
   * 生成访问令牌.
   *
   * @param uid 用户 ID
   * @return 访问令牌
   */
  Mono<String> generateToken(String uid);
}
