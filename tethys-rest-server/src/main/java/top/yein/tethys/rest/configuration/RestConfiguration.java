package top.yein.tethys.rest.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.resource.AuthInterceptor;
import top.yein.tethys.rest.server.RestServer;

/** @author KK (kzou227@qq.com) */
@Configuration(proxyBeanMethods = false)
public class RestConfiguration {

  /**
   * @param authService
   * @return
   */
  @Bean
  public Interceptors interceptors(AuthService authService) {
    var authInterceptor = new AuthInterceptor(authService);
    return new Interceptors(authInterceptor::handle);
  }

  /**
   * @param addr
   * @param interceptors
   * @return
   */
  @Bean
  public RestServer restServer(
      @Value("${rest-server.addr}") String addr, Interceptors interceptors) {
    return new RestServer(addr, interceptors);
  }
}
