package top.yein.tethys.core.http;

import reactor.netty.http.server.HttpServerRoutes;

/**
 * 服务器路由注册服务.
 *
 * @author KK (kzou227@qq.com)
 */
@FunctionalInterface
public interface RoutingService {

  /**
   * 更新 Routes.
   *
   * @param routes 服务器路由
   * @param interceptors 服务器拦截器
   */
  void update(HttpServerRoutes routes, Interceptors interceptors);
}
