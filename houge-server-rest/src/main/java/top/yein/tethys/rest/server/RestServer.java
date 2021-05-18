/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.yein.tethys.rest.server;

import com.google.common.net.HostAndPort;
import java.time.Duration;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.Env;
import top.yein.tethys.rest.http.HttpServerRoutesWrapper;
import top.yein.tethys.rest.http.Interceptors;
import top.yein.tethys.rest.http.RoutingService;

/**
 * REST 服务.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class RestServer {

  private static final int IDLE_TIMEOUT_SECS = 90;

  private final String addr;
  private final Interceptors interceptors;
  private final List<RoutingService> routingServices;

  private DisposableServer disposableServer;

  /**
   * 构造函数.
   *
   * @param addr 服务访问 IP 及地址
   * @param interceptors
   * @param routingServices
   * @see HostAndPort
   */
  public RestServer(String addr, Interceptors interceptors, List<RoutingService> routingServices) {
    this.addr = addr;
    this.interceptors = interceptors;
    this.routingServices = routingServices;
  }

  /** 启动 REST 服务. */
  public void start() {
    var hap = HostAndPort.fromString(addr);
    var routes = HttpServerRoutes.newRoutes();
    for (RoutingService routingService : routingServices) {
      log.info("更新 Routes [resource={}]", routingService);
      routingService.update(routes, interceptors);
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
