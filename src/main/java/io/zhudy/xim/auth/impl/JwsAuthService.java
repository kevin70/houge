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

import io.jsonwebtoken.*;
import io.zhudy.xim.BizCodeException;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.auth.AuthService;
import io.zhudy.xim.auth.NoneAuthContext;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Named;

import static io.zhudy.xim.BizCodes.*;
import static io.zhudy.xim.ConfigKeys.IM_SERVER_ENABLED_ANONYMOUS;

/**
 * <a href="https://tools.ietf.org/html/rfc7515">JWS</a> 用户认证服务实现.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class JwsAuthService implements AuthService {

  private final JwtParser jwtParser;
  private final boolean anonymousEnabled;

  @Inject
  public JwsAuthService(
      @Named(IM_SERVER_ENABLED_ANONYMOUS) boolean anonymousEnabled,
      SigningKeyResolver keyResolver) {
    this.anonymousEnabled = anonymousEnabled;
    jwtParser = Jwts.parserBuilder().setSigningKeyResolver(keyResolver).build();
  }

  @Override
  public Mono<Boolean> anonymousEnabled() {
    return Mono.just(anonymousEnabled);
  }

  @Override
  public Mono<AuthContext> authorize(String token) {
    if (token == null || token.isEmpty()) {
      if (anonymousEnabled) {
        return Mono.just(NoneAuthContext.INSTANCE);
      } else {
        return Mono.error(new BizCodeException(C401, "缺少访问令牌"));
      }
    }

    return Mono.create(
        sink -> {
          try {
            var jws = jwtParser.parseClaimsJws(token);
            var authContext = new JwsAuthContext(token, jws.getBody());
            sink.success(authContext);
          } catch (MalformedJwtException e) {
            sink.error(new BizCodeException(C3300, e.getMessage()));
          } catch (ExpiredJwtException e) {
            sink.error(new BizCodeException(C3301, e.getMessage()));
          } catch (PrematureJwtException e) {
            sink.error(new BizCodeException(C3302, e.getMessage()));
          } catch (Exception e) {
            sink.error(new BizCodeException(C3305, e.getMessage()));
          }
        });
  }
}
