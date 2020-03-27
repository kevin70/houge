package io.zhudy.xim.auth;

import reactor.core.publisher.Mono;

/**
 * 用户认证服务.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface AuthService {

  /**
   * 用户认证.
   *
   * @param token 请求令牌
   * @return 认证上下文
   */
  Mono<AuthContext> authorize(String token);
}
