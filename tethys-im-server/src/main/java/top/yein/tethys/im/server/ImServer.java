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

import static top.yein.tethys.core.ConfigKeys.IM_SERVER_ADDR;

import com.google.common.net.HostAndPort;
import io.netty.handler.timeout.IdleStateHandler;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.log4j.Log4j2;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.WebsocketServerSpec;

/**
 * IM 服务器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class ImServer {

  private static final int IDLE_TIMEOUT_SECS = 90;

  public static final String IM_WS_PATH = "/im";

  private final HostAndPort hap;
  private final ImSocketHandler imSocketHandler;
  private final RestHandler restHandler;
  private DisposableServer disposableServer;

  @Inject
  public ImServer(
      @Named(IM_SERVER_ADDR) HostAndPort hap,
      ImSocketHandler imSocketHandler,
      RestHandler restHandler) {
    this.hap = hap;
    this.imSocketHandler = imSocketHandler;
    this.restHandler = restHandler;
  }

  /** 启动 IM 服务. */
  public void start() {
    this.disposableServer =
        HttpServer.create()
            .host(hap.getHost())
            .port(hap.getPort())
            .wiretap(true)
            .tcpConfiguration(
                tcpServer -> {
                  var sb = tcpServer.configure();
                  sb.childHandler(
                      new IdleStateHandler(
                          IDLE_TIMEOUT_SECS, IDLE_TIMEOUT_SECS, IDLE_TIMEOUT_SECS));
                  return tcpServer;
                })
            .route(
                routes -> {
                  // 注册 REST 服务
                  this.restHandler.registerService(routes);

                  // 注册 IM Socket
                  routes.ws(IM_WS_PATH, imSocketHandler, WebsocketServerSpec.builder().build());
                })
            .bindNow();
    log.info("IM Server started at - {}", hap);
  }

  /** 停止 IM 服务. */
  public void stop() {
    if (disposableServer != null) {
      disposableServer.disposeNow();
    }
    log.info("IM Server stopped - {}", hap);
  }
}
