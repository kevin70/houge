package top.yein.tethys.rest.resource.i;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.service.UserService;
import top.yein.tethys.vo.UserCreateVo;

/**
 * 用户 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class UserResource extends AbstractRestSupport implements RoutingService {

  private final UserService userService;

  /**
   * 可以被 IoC 容器使用的构造函数.
   *
   * @param userService 用户服务对象
   */
  @Inject
  public UserResource(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.post("/i/users", interceptors.serviceAuth(this::createUser));
  }

  /**
   * 创建用户.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  Mono<Void> createUser(HttpServerRequest request, HttpServerResponse response) {
    return json(request, UserCreateVo.class)
        .flatMap(vo -> userService.createUser(vo).flatMap(dto -> json(response, dto)));
  }
}
