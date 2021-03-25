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
import top.yein.tethys.service.GroupService;
import top.yein.tethys.vo.GroupCreateVo;

/**
 * 群组 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupResource extends AbstractRestSupport implements RoutingService {

  private final GroupService groupService;

  /**
   * 可以被 IoC 容器使用的构造函数.
   *
   * @param groupService 群组服务
   */
  @Inject
  public GroupResource(GroupService groupService) {
    this.groupService = groupService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.post("/i/groups", interceptors.auth(this::createGroup));
    routes.delete("/i/groups/{groupId}", interceptors.auth(this::deleteGroup));

    routes.put("/i/group-members/{groupId}/join", interceptors.auth(this::joinMember));
    routes.delete("/i/group-members/{groupId}/join", interceptors.auth(this::removeMember));
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> createGroup(HttpServerRequest request, HttpServerResponse response) {
    return authContext()
        .zipWith(json(request, GroupCreateVo.class))
        .flatMap(
            t -> {
              var ac = t.getT1();
              var vo = t.getT2();
              return groupService.createGroup(ac.uid(), vo).flatMap(dto -> json(response, dto));
            });
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