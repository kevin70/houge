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
package top.yein.tethys.rest.resource.i;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.service.UserService;
import top.yein.tethys.vo.UserCreateVo;

/**
 * 用户 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class UserResource extends AbstractRestSupport implements RoutingService {

  private final UserService userService;

  /**
   * 可以被 IoC 容器使用的构造函数.
   *
   * @param userService 用户服务对象
   */
  @Inject
  public UserResource(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.post("/i/users", interceptors.serviceAuth(this::createUser));
  }

  /**
   * 创建用户.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> createUser(HttpServerRequest request, HttpServerResponse response) {
    return json(request, UserCreateVo.class)
        .flatMap(vo -> userService.createUser(vo).flatMap(dto -> json(response, dto)));
  }
}
