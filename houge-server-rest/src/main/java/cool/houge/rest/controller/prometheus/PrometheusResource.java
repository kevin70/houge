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
package cool.houge.rest.controller.prometheus;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import cool.houge.rest.http.Interceptors;
import cool.houge.rest.http.RoutingService;

/** @author KK (kzou227@qq.com) */
public class PrometheusResource implements RoutingService {

  private final PrometheusMeterRegistry prometheusMeterRegistry;

  /** @param prometheusMeterRegistry */
  @Inject
  public PrometheusResource(PrometheusMeterRegistry prometheusMeterRegistry) {
    this.prometheusMeterRegistry = prometheusMeterRegistry;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.get("/-/prometheus", this::prometheus);
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> prometheus(HttpServerRequest request, HttpServerResponse response) {
    return response.sendString(Mono.just(prometheusMeterRegistry.scrape())).then();
  }
}
