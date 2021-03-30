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
package top.yein.tethys.core.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.AuthContext;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.storage.JwtSecretDao;

/**
 * <a href="https://tools.ietf.org/html/rfc7515">JWS</a> 用户认证服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class JwsAuthService implements AuthService {

  private final JwtSecretDao jwtSecretDao;

  /**
   * 可以被 IoC 容器使用的构造函数.
   *
   * @param jwtSecretDao JWT 密钥存储对象
   */
  @Inject
  public JwsAuthService(JwtSecretDao jwtSecretDao) {
    this.jwtSecretDao = jwtSecretDao;
  }

  @Override
  public Mono<AuthContext> authenticate(String token) {
    if (token == null || token.isEmpty()) {
      return Mono.error(new BizCodeException(BizCode.C401, "缺少访问令牌"));
    }

    // 解码 JWT
    DecodedJWT decodedJWT;
    try {
      decodedJWT = JWT.decode(token);
    } catch (JWTDecodeException e) {
      throw new BizCodeException(BizCodes.C3300, e);
    }

    return jwtSecretDao
        .loadById(decodedJWT.getKeyId())
        .switchIfEmpty(
            Mono.error(
                () ->
                    new BizCodeException(BizCodes.C3309)
                        .addContextValue("kid", decodedJWT.getId())))
        .doOnNext(
            cachedJwtSecret -> {
              var verifier = JWT.require(cachedJwtSecret.getAlgorithm()).acceptLeeway(90).build();
              try {
                verifier.verify(decodedJWT);
              } catch (TokenExpiredException e) {
                throw new BizCodeException(BizCodes.C3301, e);
              } catch (InvalidClaimException e) {
                throw new BizCodeException(BizCodes.C3302, e);
              } catch (SignatureVerificationException e) {
                throw new BizCodeException(BizCodes.C3305, e);
              } catch (JWTVerificationException e) {
                throw new BizCodeException(BizCodes.C3300, e);
              }
            })
        .map(
            unused -> {
              long uid = parseUid(decodedJWT.getId());
              return new JwsAuthContext(uid, token, decodedJWT);
            });
  }

  private long parseUid(String id) {
    try {
      return Long.parseLong(id);
    } catch (NumberFormatException e) {
      throw new BizCodeException(BizCodes.C3300).addContextValue("jwt_id", id);
    }
  }
}
