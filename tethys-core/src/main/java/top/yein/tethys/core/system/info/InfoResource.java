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
package top.yein.tethys.core.system.info;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.system.info.InfoService;

/**
 * 系统信息 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class InfoResource extends AbstractRestSupport implements RoutingService {

  private final InfoService infoService;

  /** @param infoService */
  @Inject
  public InfoResource(InfoService infoService) {
    this.infoService = infoService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.get("/-/info", this::info);
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> info(HttpServerRequest request, HttpServerResponse response) {
    return infoService.info().flatMap(info -> json(response, info.getDetails()));
  }
}
