package top.yein.tethys.core.system.health;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.system.health.HealthService;

/**
 * 系统健康状况 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class HealthResource extends AbstractRestSupport implements RoutingService {

  private final HealthService healthService;

  /** @param healthService */
  @Inject
  public HealthResource(HealthService healthService) {
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
