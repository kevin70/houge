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

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.domain.Paging;
import top.yein.tethys.service.MessageService;
import top.yein.tethys.storage.query.UserMessageQuery;

/**
 * 消息 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class MessageResource extends AbstractRestSupport implements RoutingService {

  private final MessageService messageService;

  /**
   * 使用消息服务构建对象.
   *
   * @param messageService 消息服务
   */
  @Inject
  public MessageResource(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.get("/messages", interceptors.auth(this::queryByUser));
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> queryByUser(HttpServerRequest request, HttpServerResponse response) {
    return authContext()
        .flatMap(
            ac -> {
              var beginTime = queryDateTime(request, "begin_time", () -> null);
              var offset = queryInt(request, "offset", 0);
              var limit = queryInt(request, "limit", 500);
              var q = UserMessageQuery.builder().uid(ac.uid()).beginTime(beginTime).build();
              return messageService
                  .queryByUser(q, Paging.of(offset, limit))
                  .collectList()
                  .flatMap(messages -> json(response, messages));
            });
  }
}
