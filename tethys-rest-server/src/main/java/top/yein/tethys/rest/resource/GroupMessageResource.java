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

import java.time.LocalDateTime;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.query.GroupMessageQuery;
import top.yein.tethys.service.GroupMessageService;

/**
 * 群组聊天 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupMessageResource extends AbstractRestSupport implements RoutingService {

  private final GroupMessageService groupMessageService;

  /**
   * 构造函数.
   *
   * @param groupMessageService 群组消息服务
   */
  @Inject
  public GroupMessageResource(GroupMessageService groupMessageService) {
    this.groupMessageService = groupMessageService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    // 群组聊天
    routes.get("/group-messages/recent", interceptors.auth(this::findRecentMessages));
  }

  /**
   * 查询群组消息.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  public Mono<Void> findRecentMessages(HttpServerRequest request, HttpServerResponse response) {
    return authContext()
        .flatMap(
            ac -> {
              var createTime =
                  queryDateTime(request, "create_time", () -> LocalDateTime.now().minusDays(3));
              // 查询对象
              var query = new GroupMessageQuery();
              query.setGroupId(requiredQueryParam(request, "group_id"));
              query.setCreateTime(createTime);
              query.setLimit(queryInt(request, "limit", 500));
              query.setOffset(queryInt(request, "offset", 0));

              return groupMessageService
                  .findRecentMessages(query)
                  .collectList()
                  .flatMap(privateMessages -> json(response, privateMessages));
            });
  }
}
