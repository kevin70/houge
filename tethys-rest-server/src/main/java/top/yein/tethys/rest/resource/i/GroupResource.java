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

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;

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
import top.yein.tethys.vo.GroupJoinMemberVo;

/**
 * 群组 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupResource extends AbstractRestSupport implements RoutingService {

  /** Path 参数名称. */
  private static final String GROUP_ID_PN = "groupId";

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
    routes.post("/i/groups", interceptors.serviceAuth(this::createGroup));
    routes.delete("/i/groups/{groupId}", interceptors.serviceAuth(this::deleteGroup));

    routes.put("/i/group-members/{groupId}/join", interceptors.serviceAuth(this::joinMember));
    routes.delete("/i/group-members/{groupId}/join", interceptors.serviceAuth(this::removeMember));
  }

  /**
   * 创建群组.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> createGroup(HttpServerRequest request, HttpServerResponse response) {
    return json(request, GroupCreateVo.class)
        .flatMap(vo -> groupService.createGroup(vo).flatMap(dto -> json(response, dto)));
  }

  /**
   * 删除群组.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> deleteGroup(HttpServerRequest request, HttpServerResponse response) {
    var groupId = pathLong(request, GROUP_ID_PN);
    return groupService
        .deleteGroup(groupId)
        .then(Mono.defer(() -> response.status(NO_CONTENT).send()));
  }

  /**
   * 将指定的用户与群组建立关系.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> joinMember(HttpServerRequest request, HttpServerResponse response) {
    return json(request, GroupJoinMemberVo.class)
        .flatMap(
            vo -> {
              var groupId = pathLong(request, GROUP_ID_PN);
              return groupService
                  .joinMember(groupId, vo)
                  .then(Mono.defer(() -> response.status(NO_CONTENT).send()));
            });
  }

  /**
   * 将指定的用户与群组解除关系.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> removeMember(HttpServerRequest request, HttpServerResponse response) {
    return json(request, GroupJoinMemberVo.class)
        .flatMap(
            vo -> {
              var groupId = pathLong(request, GROUP_ID_PN);
              return groupService
                  .removeMember(groupId, vo)
                  .then(Mono.defer(() -> response.status(NO_CONTENT).send()));
            });
  }
}
