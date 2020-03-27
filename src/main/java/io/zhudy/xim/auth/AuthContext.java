package io.zhudy.xim.auth;

import javax.annotation.Nonnull;

/**
 * 认证的上下文信息.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface AuthContext {

  /** 未认证或匿名访问的上下文信息. */
  AuthContext NONE_AUTH_CONTEXT =
      new AuthContext() {

        @Override
        public String uid() {
          throw new IllegalStateException("匿名访问");
        }

        @Override
        public String token() {
          throw new IllegalStateException("匿名访问");
        }
      };

  /**
   * 返回用户 ID.
   *
   * @return 用户 ID
   */
  @Nonnull
  String uid();

  /**
   * 返回认证令牌.
   *
   * @return 认证令牌
   */
  @Nonnull
  String token();

  /**
   * 是否为匿名认证.
   *
   * @return true 是匿名认证
   */
  default boolean isAnonymous() {
    return true;
  }
}
