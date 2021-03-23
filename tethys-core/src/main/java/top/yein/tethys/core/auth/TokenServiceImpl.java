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

import io.jsonwebtoken.Jwts;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.Nil;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.entity.User;
import top.yein.tethys.storage.JwtSecretDao;
import top.yein.tethys.storage.UserDao;
import top.yein.tethys.storage.query.UserQueryDao;

/**
 * 访问令牌实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class TokenServiceImpl implements TokenService {

  private final TokenProps tokenProps;
  private final JwtSecretDao jwtSecretDao;
  private final UserDao userDao;
  private final UserQueryDao userQueryDao;

  /**
   * 使用 JWT 密钥数据访问对象构建对象.
   *
   * @param tokenProps 令牌配置.
   * @param jwtSecretDao JWT 密钥数据访问对象
   * @param userDao 用户访问数据对象
   * @param userQueryDao 用户查询数据访问对象
   */
  @Inject
  public TokenServiceImpl(
      TokenProps tokenProps,
      JwtSecretDao jwtSecretDao,
      UserDao userDao,
      UserQueryDao userQueryDao) {
    this.tokenProps = tokenProps;
    this.jwtSecretDao = jwtSecretDao;
    this.userDao = userDao;
    this.userQueryDao = userQueryDao;
  }

  @Override
  public Mono<String> generateToken(long uid) {
    if (!tokenProps.getGenerator().isTestEnabled()) {
      return Mono.error(new BizCodeException(BizCodes.C403, "当前运行环境禁止访问测试令牌生成接口"));
    }

    var emptyInsert =
        Mono.defer(
            () -> {
              // TIPS: 这里是否需要增加额外的配置开关, 确认是否开启自动保存用户信息
              var entity = new User();
              entity.setId(uid);
              entity.setOriginUid(String.valueOf(uid));
              return userDao.insert(entity).then(Nil.mono());
            });

    return userQueryDao
        .existsById(uid)
        .switchIfEmpty(emptyInsert)
        .flatMap(unused -> this.generateToken0(uid));
  }

  private Mono<String> generateToken0(long uid) {
    return jwtSecretDao
        .loadNoDeleted()
        .switchIfEmpty(Flux.error(() -> new BizCodeException(BizCodes.C3310)))
        .next()
        .map(
            cachedJwtSecret -> {
              Map<String, Object> header = Jwts.jwsHeader().setKeyId(cachedJwtSecret.getId());
              var claims = Jwts.claims().setId(String.valueOf(uid));
              var token =
                  Jwts.builder()
                      .signWith(cachedJwtSecret.getSecretKey(), cachedJwtSecret.getAlgorithm())
                      .setHeader(header)
                      .setClaims(claims)
                      .compact();

              log.info(
                  "生成访问令牌 [kid={}, uid={}, access_token={}]", cachedJwtSecret.getId(), uid, token);
              return token;
            });
  }
}
