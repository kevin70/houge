/*
 * Copyright 2019-2021 the original author or authors
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
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.storage.JwtSecretDao;
import top.yein.tethys.storage.query.UserQueryDao;

/**
 * 访问令牌实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class TokenServiceImpl implements TokenService {

  private final JwtSecretDao jwtSecretDao;
  private final UserQueryDao userQueryDao;

  /**
   * 使用 JWT 密钥数据访问对象构建对象.
   *
   * @param jwtSecretDao JWT 密钥数据访问对象
   * @param userQueryDao 用户查询数据访问对象
   */
  @Inject
  public TokenServiceImpl(JwtSecretDao jwtSecretDao, UserQueryDao userQueryDao) {
    this.jwtSecretDao = jwtSecretDao;
    this.userQueryDao = userQueryDao;
  }

  @Override
  public Mono<String> generateToken(long uid) {
    return userQueryDao
        .existsById(uid)
        .switchIfEmpty(Mono.error(() -> new BizCodeException(BizCode.C404, "用户不存在")))
        .flatMap(unused -> this.generateToken0(uid));
  }

  private Mono<String> generateToken0(long uid) {
    return jwtSecretDao
        .loadNoDeleted()
        .switchIfEmpty(Flux.error(() -> new BizCodeException(BizCodes.C3310)))
        .next()
        .map(
            cachedJwtAlgorithm -> {
              var token =
                  JWT.create()
                      .withKeyId(cachedJwtAlgorithm.getId())
                      .withJWTId(String.valueOf(uid))
                      .sign(cachedJwtAlgorithm.getAlgorithm());
              log.info("生成访问令牌 [kid={}, uid={}]", cachedJwtAlgorithm.getId(), uid);
              return token;
            });
  }
}
