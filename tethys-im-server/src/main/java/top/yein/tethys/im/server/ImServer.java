/*
 * Copyright 2019-2020 the original author or authors
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
package top.yein.tethys.im.server;

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
import reactor.netty.http.server.WebsocketServerSpec;
import top.yein.tethys.core.Env;
import top.yein.tethys.core.http.HttpServerRoutesWrapper;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;

/**
 * IM Server.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public final class ImServer implements ApplicationContextAware {

  private static final int IDLE_TIMEOUT_SECS = 90;
  public static final String IM_WS_PATH = "/im";

  private final String addr;
  private final WebsocketHandler websocketHandler;
  private final Interceptors interceptors;
  /** spring 应用上下文. */
  private ApplicationContext applicationContext;

  private DisposableServer disposableServer;

  /**
   * @param addr
   * @param websocketHandler
   * @param interceptors
   */
  public ImServer(String addr, WebsocketHandler websocketHandler, Interceptors interceptors) {
    this.addr = addr;
    this.websocketHandler = websocketHandler;
    this.interceptors = interceptors;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /** 启动 IM 服务. */
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

    // ws 注册
    routes.ws(
        IM_WS_PATH,
        websocketHandler::handle,
        WebsocketServerSpec.builder().handlePing(false).build());

    this.disposableServer =
        HttpServer.create()
            .host(hap.getHost())
            .port(hap.getPort())
            .wiretap(Env.current() != Env.PROD)
            .idleTimeout(Duration.ofSeconds(IDLE_TIMEOUT_SECS))
            .handle(new HttpServerRoutesWrapper(routes))
            .bindNow();
    log.info("IM Server 启动完成 - {}", hap);
  }

  /** 停止 IM 服务. */
  public void stop() {
    if (disposableServer != null) {
      disposableServer.disposeNow();
    }
    log.info("IM Server 停止完成 - {}", addr);
  }
}
