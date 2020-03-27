package io.zhudy.xim.auth.impl;

import io.jsonwebtoken.Claims;
import io.zhudy.xim.auth.AuthContext;
import javax.annotation.Nonnull;

/**
 * JWS 认证上下文实现.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
class JwsAuthContext implements AuthContext {

  private final Claims claims;
  private final String token;

  JwsAuthContext(String token, Claims claims) {
    this.token = token;
    this.claims = claims;
  }

  @Override
  public String uid() {
    return claims.getId();
  }

  @Nonnull
  @Override
  public String token() {
    return token;
  }

  @Override
  public boolean isAnonymous() {
    return false;
  }
}
