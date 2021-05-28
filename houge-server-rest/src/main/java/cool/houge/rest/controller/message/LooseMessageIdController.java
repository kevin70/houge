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

import cool.houge.id.MessageIdGenerator;
import cool.houge.rest.http.AbstractRestSupport;
import cool.houge.rest.http.Interceptors;
import cool.houge.rest.http.RoutingService;
import java.util.Optional;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;

/**
 * 消息 ID REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class LooseMessageIdController extends AbstractRestSupport implements RoutingService {

  private final MessageIdGenerator messageIdGenerator;

  /**
   * 构造函数.
   *
   * @param messageIdGenerator 消息 ID 生成器
   */
  @Inject
  public LooseMessageIdController(MessageIdGenerator messageIdGenerator) {
    this.messageIdGenerator = messageIdGenerator;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.get("/p/message-ids", interceptors.userAuth(this::getMessageIds));
  }

  /**
   * 获取消息 ID 列表.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  public Mono<Void> getMessageIds(HttpServerRequest request, HttpServerResponse response) {
    var limit =
        Optional.ofNullable(queryParam(request, "limit"))
            .map(
                v -> {
                  try {
                    return Integer.parseInt(v);
                  } catch (NumberFormatException e) {
                    throw new BizCodeException(BizCode.C910);
                  }
                })
            .orElse(MessageIdGenerator.REQUEST_IDS_LIMIT);
    return messageIdGenerator
        .nextIds()
        .limitRequest(limit)
        .collectList()
        .flatMap(ids -> json(response, ids));
  }
}
