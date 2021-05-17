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
package top.yein.tethys.auth.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.annotations.VisibleForTesting;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;
import reactor.util.context.ContextView;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.BizCodes;
import top.yein.tethys.auth.AuthContext;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.storage.JwtSecretDao;
import top.yein.tethys.storage.entity.JwtSecret;
import top.yein.tethys.storage.query.UserQueryDao;

/**
 * <a href="https://tools.ietf.org/html/rfc7515">JWS</a> 用户认证服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class JwsAuthService implements AuthService, TokenService {

  /** 缓存默认刷新周期. */
  private static final Duration REFRESH_CACHE_DURATION = Duration.ofMinutes(5);

  private final JwtSecretDao jwtSecretDao;
  private final UserQueryDao userQueryDao;
  private final AsyncCache<String, CachedJwtAlgorithm> jwtAlgorithmCache;

  /**
   * 可以被 IoC 容器使用的构造函数.
   *
   * @param jwtSecretDao JWT 密钥存储对象
   * @param userQueryDao 用户查询数据访问对象
   */
  @Inject
  public JwsAuthService(JwtSecretDao jwtSecretDao, UserQueryDao userQueryDao) {
    this.jwtSecretDao = jwtSecretDao;
    this.userQueryDao = userQueryDao;
    this.jwtAlgorithmCache =
        Caffeine.newBuilder()
            .recordStats()
            .refreshAfterWrite(REFRESH_CACHE_DURATION)
            .maximumSize(Byte.MAX_VALUE)
            .buildAsync(cacheMappingFunc(Context.empty())::apply);
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

    return Mono.fromFuture(this.jwtAlgorithmCache.get(decodedJWT.getKeyId(), cacheLoadFunc()))
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

  @Override
  public Mono<String> generateToken(long uid) {
    return userQueryDao
        .existsById(uid)
        .switchIfEmpty(Mono.error(() -> new BizCodeException(BizCode.C404, "用户不存在")))
        .flatMap(unused -> this.generateToken0(uid));
  }

  private long parseUid(String id) {
    try {
      return Long.parseLong(id);
    } catch (NumberFormatException e) {
      throw new BizCodeException(BizCodes.C3300).addContextValue("jwt_id", id);
    }
  }

  private BiFunction<String, Executor, CompletableFuture<CachedJwtAlgorithm>> cacheMappingFunc(
      ContextView context) {
    return (s, executor) -> {
      log.debug("加载 jwt secret [kid={}]", s);
      return jwtSecretDao
          .findById(s)
          .map(this::toCachedJwtAlgorithm)
          // context 用于事务传递
          .contextWrite(context)
          .toFuture();
    };
  }

  @VisibleForTesting
  CachedJwtAlgorithm toCachedJwtAlgorithm(JwtSecret e) {
    var name = e.getAlgorithm();
    Algorithm algorithm;
    if ("HS256".equals(name)) {
      algorithm = Algorithm.HMAC256(e.getSecretKey().array());
    } else if ("HS512".equals(name)) {
      algorithm = Algorithm.HMAC512(e.getSecretKey().array());
    } else {
      throw new BizCodeException(BizCode.C404).addContextValue("algorithm", name);
    }
    return CachedJwtAlgorithm.builder()
        .id(e.getId())
        .algorithm(algorithm)
        .deleted(e.getDeleted())
        .build();
  }

  private BiFunction<String, Executor, CompletableFuture<CachedJwtAlgorithm>> cacheLoadFunc() {
    return (s, executor) ->
        this.jwtSecretDao
            .findById(s)
            .map(this::toCachedJwtAlgorithm)
            .subscribeOn(Schedulers.fromExecutor(executor))
            .toFuture();
  }

  private Mono<String> generateToken0(long uid) {
    return Flux.fromIterable(this.jwtAlgorithmCache.asMap().values())
        .flatMap(Mono::fromFuture)
        .switchIfEmpty(this.loadAll())
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

  private Flux<CachedJwtAlgorithm> loadAll() {
    return Flux.defer(
        () ->
            this.jwtSecretDao
                .findAll()
                .map(this::toCachedJwtAlgorithm)
                .switchIfEmpty(Flux.error(() -> new BizCodeException(BizCodes.C3310)))
                .doOnNext(
                    cachedJwtAlgorithm ->
                        this.jwtAlgorithmCache.put(
                            cachedJwtAlgorithm.getId(),
                            CompletableFuture.completedFuture(cachedJwtAlgorithm))));
  }
}
