package top.yein.tethys.core.system.resource;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.system.health.HealthService;

/**
 * 系统健康状况 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class HealthResource extends AbstractRestSupport {

  private final HealthService healthService;

  public HealthResource(HealthService healthService) {
    this.healthService = healthService;
  }

  /**
   * @param request
   * @param response
   * @return
   */
  public Mono<Void> health(HttpServerRequest request, HttpServerResponse response) {
    return healthService
        .health(queryParam(request, "debug") != null)
        .flatMap(healthComposite -> json(response, healthComposite));
  }
}
