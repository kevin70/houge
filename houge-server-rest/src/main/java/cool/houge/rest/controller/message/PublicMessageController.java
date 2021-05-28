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
package cool.houge.rest.controller.message;

import cool.houge.domain.Paging;
import cool.houge.rest.http.AbstractRestSupport;
import cool.houge.rest.http.Interceptors;
import cool.houge.rest.http.RoutingService;
import cool.houge.service.message.MessageService;
import cool.houge.service.message.ReadMessageInput;
import cool.houge.service.message.SendMessageInput;
import cool.houge.storage.query.UserMessageQuery;
import io.netty.handler.codec.http.HttpResponseStatus;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;

/**
 * 向用户开放的消息 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class PublicMessageController extends AbstractRestSupport implements RoutingService {

  private static final Logger log = LogManager.getLogger();
  private final MessageService messageService;

  /**
   * 可以被 IoC 容器使用的构造函数.
   *
   * @param messageService 消息服务
   */
  @Inject
  public PublicMessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.get("/p/messages", interceptors.auth(this::queryByUser));
    routes.get("/p/messages/read", interceptors.auth(this::readMessages));
    routes.post("/p/messages/send", interceptors.auth(this::sendMessage));
  }

  /**
   * 查询用户指定时间之后的消息.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
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

  /**
   * 批量将指定消息设置为已读状态.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> readMessages(HttpServerRequest request, HttpServerResponse response) {
    return authContext()
        .zipWith(json(request, ReadMessageInput.class))
        .flatMap(
            t -> {
              var ac = t.getT1();
              var vo = t.getT2();
              return messageService
                  .readMessages(ac.uid(), vo.getMessageIds())
                  .then(response.status(HttpResponseStatus.NO_CONTENT).send());
            });
  }

  /**
   * 发送聊天消息.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> sendMessage(HttpServerRequest request, HttpServerResponse response) {
    return authContext()
        .zipWith(json(request, SendMessageInput.class))
        .flatMap(
            t -> {
              var ac = t.getT1();
              var vo = t.getT2();
              log.debug("发送聊天消息 uid={} vo={}", ac.uid(), vo);
              //              return remoteMessageService
              //                  .sendMessage(ac.uid(), vo)
              //                  .flatMap(result -> json(response, result));
              return Mono.empty();
            });
  }
}
