package top.yein.tethys.core.system.prometheus;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;

/** @author KK (kzou227@qq.com) */
@Component
public class PrometheusResource implements RoutingService {

  private final PrometheusMeterRegistry prometheusMeterRegistry;

  /** @param prometheusMeterRegistry */
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
