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
package top.yein.tethys.rest.resource;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;

/**
 * 群组 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupResource extends AbstractRestSupport implements RoutingService {

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.post("/groups", interceptors.auth(this::createGroup));
    routes.delete("/groups/{groupId}", interceptors.auth(this::deleteGroup));

    routes.put("/group-members/{groupId}/join", interceptors.auth(this::joinMember));
    routes.delete("/group-members/{groupId}/join", interceptors.auth(this::removeMember));
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> createGroup(HttpServerRequest request, HttpServerResponse response) {
    return Mono.empty();
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> deleteGroup(HttpServerRequest request, HttpServerResponse response) {
    return Mono.empty();
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> joinMember(HttpServerRequest request, HttpServerResponse response) {
    return Mono.empty();
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> removeMember(HttpServerRequest request, HttpServerResponse response) {
    return Mono.empty();
  }
}
