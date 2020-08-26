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

import static io.zhudy.xim.BizCodes.C3300;
import static io.zhudy.xim.BizCodes.C3301;
import static io.zhudy.xim.BizCodes.C3302;
import static io.zhudy.xim.BizCodes.C3305;
import static io.zhudy.xim.BizCodes.C401;
import static io.zhudy.xim.ConfigKeys.IM_SERVER_ENABLED_ANONYMOUS;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.SigningKeyResolver;
import io.zhudy.xim.BizCodeException;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.auth.AuthService;
import javax.inject.Inject;
import javax.inject.Named;
import reactor.core.publisher.Mono;

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
        return Mono.just(AuthContext.NONE_AUTH_CONTEXT);
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
