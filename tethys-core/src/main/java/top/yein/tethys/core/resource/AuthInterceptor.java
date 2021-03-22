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
package top.yein.tethys.core.resource;

import java.util.function.BiFunction;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.util.context.Context;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.http.AbstractRestSupport;

/**
 * 用户认证拦截器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class AuthInterceptor extends AbstractRestSupport {

  private static final String ACCESS_TOKEN_QUERY_NAME = "access_token";

  private final AuthService authService;

  /**
   * 构造函数.
   *
   * @param authService 认证服务
   */
  @Inject
  public AuthInterceptor(AuthService authService) {
    this.authService = authService;
  }

  /**
   * 请求认证.
   *
   * @param next 认证成功后执行的处理函数
   * @return RS
   */
  public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> handle(
      BiFunction<? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>>
          next) {
    return (request, response) ->
        Mono.defer(
            () -> {
              var accessToken = queryParam(request, ACCESS_TOKEN_QUERY_NAME);
              if (accessToken == null || accessToken.isEmpty()) {
                throw new BizCodeException(BizCode.C401, "缺少 access_token");
              }
              return authService
                  .authenticate(accessToken)
                  .flatMap(
                      (authContext) ->
                          Mono.defer(() -> Mono.from(next.apply(request, response)))
                              .contextWrite(Context.of(AUTH_CONTEXT_KEY, authContext)));
            });
  }
}
