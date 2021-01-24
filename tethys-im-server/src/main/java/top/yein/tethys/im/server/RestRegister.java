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
package top.yein.tethys.im.server;

import java.util.function.Consumer;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.resource.TokenResource;

/**
 * RESTFul 接口处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class RestRegister implements Consumer<HttpServerRoutes> {

  private final TokenResource tokenResource;

  public RestRegister(TokenResource tokenResource) {
    this.tokenResource = tokenResource;
  }

  @Override
  public void accept(HttpServerRoutes routes) {
    // 访问令牌
    routes.post("/token/{uid}", tokenResource::generateToken);
  }
}
