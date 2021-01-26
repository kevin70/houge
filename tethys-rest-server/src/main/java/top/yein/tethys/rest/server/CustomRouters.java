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
package top.yein.tethys.rest.server;

import java.util.function.Consumer;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.resource.AuthenticationInterceptor;
import top.yein.tethys.core.resource.TokenResource;
import top.yein.tethys.rest.resource.MessageIdResource;

/**
 * RESTFul 接口处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class CustomRouters implements Consumer<HttpServerRoutes> {

  private final AuthenticationInterceptor authenticationInterceptor;
  private final TokenResource tokenResource;
  private final MessageIdResource messageIdResource;

  public CustomRouters(
      AuthenticationInterceptor authenticationInterceptor,
      TokenResource tokenResource,
      MessageIdResource messageIdResource) {
    this.authenticationInterceptor = authenticationInterceptor;
    this.tokenResource = tokenResource;
    this.messageIdResource = messageIdResource;
  }

  @Override
  public void accept(HttpServerRoutes routes) {
    // 访问令牌
    routes.post("/token/{uid}", tokenResource::generateToken);
    routes.get("/message-ids", authenticationInterceptor.handle(messageIdResource::getMessageIds));
  }
}
