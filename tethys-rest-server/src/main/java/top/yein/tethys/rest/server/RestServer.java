package top.yein.tethys.rest.server;

import com.google.common.net.HostAndPort;
import java.time.Duration;
import java.util.Map.Entry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.Env;
import top.yein.tethys.core.http.HttpServerRoutesWrapper;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;

/**
 * REST 服务.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class RestServer implements ApplicationContextAware {

  private static final int IDLE_TIMEOUT_SECS = 90;

  private final String addr;
  private final Interceptors interceptors;
  /** spring 应用上下文. */
  private ApplicationContext applicationContext;

  private DisposableServer disposableServer;

  /**
   * 构造函数.
   *
   * @param addr 服务访问 IP 及地址
   * @param interceptors
   * @see HostAndPort
   */
  public RestServer(String addr, Interceptors interceptors) {
    this.addr = addr;
    this.interceptors = interceptors;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /** 启动 REST 服务. */
  public void start() {
    var hap = HostAndPort.fromString(addr);
    var routes = HttpServerRoutes.newRoutes();
    if (applicationContext != null) {
      var beans = applicationContext.getBeansOfType(RoutingService.class);
      for (Entry<String, RoutingService> entry : beans.entrySet()) {
        log.info("更新 Routes [beanName={}, resource={}]", entry.getKey(), entry.getValue());
        entry.getValue().update(routes, interceptors);
      }
    }

    this.disposableServer =
        HttpServer.create()
            .host(hap.getHost())
            .port(hap.getPort())
            .wiretap(Env.current() != Env.PROD)
            .idleTimeout(Duration.ofSeconds(IDLE_TIMEOUT_SECS))
            .handle(new HttpServerRoutesWrapper(routes))
            .bindNow();
    log.info("REST Server 启动完成 - {}", hap);
  }

  /** 停止 REST 服务. */
  public void stop() {
    if (disposableServer != null) {
      disposableServer.disposeNow();
    }
    log.info("REST Server 停止完成 - {}", addr);
  }
}
