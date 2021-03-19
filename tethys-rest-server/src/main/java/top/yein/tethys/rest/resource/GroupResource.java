package top.yein.tethys.rest.resource;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;

/**
 * 群组 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupResource extends AbstractRestSupport implements RoutingService {

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.post("/groups", interceptors.auth(this::createGroup));
    routes.delete("/groups/{groupId}", interceptors.auth(this::deleteGroup));

    routes.put("/group-members/{groupId}/join", interceptors.auth(this::joinMember));
    routes.delete("/group-members/{groupId}/join", interceptors.auth(this::removeMember));
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> createGroup(HttpServerRequest request, HttpServerResponse response) {
    return Mono.empty();
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> deleteGroup(HttpServerRequest request, HttpServerResponse response) {
    return Mono.empty();
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> joinMember(HttpServerRequest request, HttpServerResponse response) {
    return Mono.empty();
  }

  /**
   * @param request
   * @param response
   * @return
   */
  Mono<Void> removeMember(HttpServerRequest request, HttpServerResponse response) {
    return Mono.empty();
  }
}
