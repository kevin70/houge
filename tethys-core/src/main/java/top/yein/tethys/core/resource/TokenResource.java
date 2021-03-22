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

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.dto.AccessTokenDto;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;

/**
 * 访问令牌 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class TokenResource extends AbstractRestSupport implements RoutingService {

  private final TokenService tokenService;

  /**
   * 默认构造函数.
   *
   * @param tokenService 令牌服务
   */
  @Inject
  public TokenResource(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.post("/token/{uid}", this::generateToken);
  }

  /**
   * 生成访问令牌.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  public Mono<Void> generateToken(HttpServerRequest request, HttpServerResponse response) {
    var uidStr = request.param("uid");
    if (uidStr == null || uidStr.isEmpty()) {
      throw new BizCodeException(BizCodes.C912);
    }

    Long uid;
    try {
      uid = Long.parseLong(uidStr);
    } catch (NumberFormatException e) {
      throw new BizCodeException(BizCodes.C911, "uid 必须为一个 number 类型");
    }

    return tokenService
        .generateToken(uid)
        .flatMap(s -> json(response, new AccessTokenDto().setAccessToken(s)));
  }
}
