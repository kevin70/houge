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
package cool.houge.rest.controller.health;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import cool.houge.rest.http.AbstractRestSupport;
import cool.houge.rest.controller.Interceptors;
import cool.houge.rest.controller.RoutingService;
import cool.houge.system.health.HealthService;

/**
 * 系统健康状况 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class HealthController extends AbstractRestSupport implements RoutingService {

  private final HealthService healthService;

  /** @param healthService */
  @Inject
  public HealthController(HealthService healthService) {
    this.healthService = healthService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.get("/-/health", this::health);
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> health(HttpServerRequest request, HttpServerResponse response) {
    return healthService
        .health(queryParam(request, "debug") != null)
        .flatMap(healthComposite -> json(response, healthComposite));
  }
}
