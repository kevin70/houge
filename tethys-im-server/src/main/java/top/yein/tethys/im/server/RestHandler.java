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
package top.yein.tethys.im.server;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;

/**
 * RESTFul 接口处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class RestHandler {

  /**
   * 注册 REST 服务接口.
   *
   * @param routes 路由器
   */
  public void registerService(HttpServerRoutes routes) {
    routes.get("/info", this::info).get("/health", this::health);
  }

  // FIXME 完善 REST 处理
  private Publisher<Void> info(HttpServerRequest request, HttpServerResponse response) {
    return response
        .header("content-type", "application/json")
        .sendHeaders()
        .sendString(Mono.just("{\"xim_version\": \"1.0.0\"}"));
  }

  private Publisher<Void> health(HttpServerRequest request, HttpServerResponse response) {
    return response
        .header("content-type", "application/json")
        .sendHeaders()
        .sendString(Mono.just("{\"xim_version\": \"1.0.0\"}"));
  }
}
