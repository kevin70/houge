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
package cool.houge.rest.controller.group;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;

import cool.houge.rest.http.AbstractRestSupport;
import cool.houge.rest.http.Interceptors;
import cool.houge.rest.http.RoutingService;
import cool.houge.service.group.CreateGroupInput;
import cool.houge.service.group.GroupService;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;

/**
 * 群组 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupController extends AbstractRestSupport implements RoutingService {

  /** Path 参数名称. */
  private static final String GROUP_ID_PN = "groupId";

  private final GroupService groupService;

  /**
   * 可以被 IoC 容器使用的构造函数.
   *
   * @param groupService 群组服务
   */
  @Inject
  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.post("/i/groups", interceptors.serverAuth(this::create));
    routes.delete("/i/groups/{groupId}", interceptors.serverAuth(this::delete));

    routes.put("/i/group-members/{groupId}/join", interceptors.serverAuth(this::joinMember));
    routes.delete("/i/group-members/{groupId}/join", interceptors.serverAuth(this::deleteMember));
  }

  /**
   * 创建群组.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> create(HttpServerRequest request, HttpServerResponse response) {
    return json(request, CreateGroupInput.class)
        .flatMap(vo -> groupService.create(vo).flatMap(dto -> json(response, dto)));
  }

  /**
   * 删除群组.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> delete(HttpServerRequest request, HttpServerResponse response) {
    var groupId = pathLong(request, GROUP_ID_PN);
    return groupService.delete(groupId).then(Mono.defer(() -> response.status(NO_CONTENT).send()));
  }

  /**
   * 将指定的用户与群组建立关系.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> joinMember(HttpServerRequest request, HttpServerResponse response) {
    return json(request, JoinMemberData.class)
        .flatMap(
            vo -> {
              var groupId = pathLong(request, GROUP_ID_PN);
              var bean = GroupBeanMapper.INSTANCE.map(vo, groupId);
              return groupService
                  .joinMember(bean)
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
  Mono<Void> deleteMember(HttpServerRequest request, HttpServerResponse response) {
    return json(request, JoinMemberData.class)
        .flatMap(
            vo -> {
              var groupId = pathLong(request, GROUP_ID_PN);
              var bean = GroupBeanMapper.INSTANCE.map(vo, groupId);
              return groupService
                  .deleteMember(bean)
                  .then(Mono.defer(() -> response.status(NO_CONTENT).send()));
            });
  }
}
