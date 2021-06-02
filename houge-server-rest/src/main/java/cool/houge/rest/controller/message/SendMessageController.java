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

import cool.houge.rest.controller.Interceptors;
import cool.houge.rest.controller.RoutingService;
import cool.houge.rest.http.AbstractRestSupport;
import cool.houge.service.message.SendMessageService;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;

/**
 * 发送消息控制器.
 *
 * @author KK (kzou227@qq.com)
 */
public class SendMessageController extends AbstractRestSupport implements RoutingService {

  private static final Logger log = LogManager.getLogger();

  private final SendMessageService sendMessageService;

  /** @param sendMessageService */
  public @Inject SendMessageController(SendMessageService sendMessageService) {
    this.sendMessageService = sendMessageService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.post("/p/messages/user/send", interceptors.userAuth(this::sendUserMessage));
    routes.post("/p/messages/group/send", interceptors.userAuth(this::sendGroupMessage));
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> sendUserMessage(HttpServerRequest request, HttpServerResponse response) {
    return authContext()
        .zipWith(json(request, SendMessageBody.class))
        .flatMap(
            t -> {
              var ac = t.getT1();
              var body = t.getT2();

              log.debug("发送聊天消息 uid={} vo={}", ac.uid(), body);
              sendMessageService.sendToUser(MessageMapper.INSTANCE.mapToUser(body, ac.uid()));
              return Mono.empty();
            });
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> sendGroupMessage(HttpServerRequest request, HttpServerResponse response) {
    return authContext()
        .zipWith(json(request, SendMessageBody.class))
        .flatMap(
            t -> {
              var ac = t.getT1();
              var body = t.getT2();
              log.debug("发送聊天消息 uid={} vo={}", ac.uid(), body);
              sendMessageService.sendToUser(MessageMapper.INSTANCE.mapToGroup(body, ac.uid()));
              return Mono.empty();
            });
  }
}
