/*
 * Copyright 2019-2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
