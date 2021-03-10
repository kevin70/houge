package top.yein.tethys.core.system.info;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;

/**
 * 系统信息 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
@Component
public class InfoResource extends AbstractRestSupport implements RoutingService {

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


    return Mono.empty();
  }
}
