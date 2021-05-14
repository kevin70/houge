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
package top.yein.tethys.rest.http;

import reactor.netty.http.server.HttpServerRoutes;

/**
 * 服务器路由注册服务.
 *
 * @author KK (kzou227@qq.com)
 */
@FunctionalInterface
public interface RoutingService {

  /**
   * 更新 Routes.
   *
   * @param routes 服务器路由
   * @param interceptors 服务器拦截器
   */
  void update(HttpServerRoutes routes, Interceptors interceptors);
}
