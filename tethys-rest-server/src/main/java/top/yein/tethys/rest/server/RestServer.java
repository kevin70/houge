package top.yein.tethys.rest.server;

import com.google.common.net.HostAndPort;
import java.time.Duration;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.Env;
import top.yein.tethys.core.http.HttpServerRoutesWrapper;

/** @author KK (kzou227@qq.com) */
@Log4j2
public class RestServer {

  private static final int IDLE_TIMEOUT_SECS = 90;

  private final String addr;
  private final CustomRouters customRouters;
  private DisposableServer disposableServer;

  @Inject
  public RestServer(String addr, CustomRouters customRouters) {
    this.addr = addr;
    this.customRouters = customRouters;
  }

  /** 启动 REST 服务. */
  public void start() {
    var hap = HostAndPort.fromString(addr);

    var routes = HttpServerRoutes.newRoutes();
    customRouters.accept(routes);

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
