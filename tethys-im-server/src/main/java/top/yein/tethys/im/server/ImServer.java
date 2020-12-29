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
import io.netty.handler.timeout.IdleStateHandler;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.WebsocketServerSpec;
import top.yein.tethys.core.Env;
import top.yein.tethys.core.netty.HttpExceptionHandler;
import top.yein.tethys.core.resource.TokenResource;

/**
 * IM Server.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public final class ImServer {

  private static final int IDLE_TIMEOUT_SECS = 90;
  public static final String IM_WS_PATH = "/im";

  private final String addr;
  private final WebsocketHandler websocketHandler;
  private final RestHandler restHandler;
  private DisposableServer disposableServer;

  /**
   * @param addr
   * @param websocketHandler
   * @param restHandler
   */
  @Inject
  public ImServer(String addr, WebsocketHandler websocketHandler, RestHandler restHandler) {
    this.addr = addr;
    this.websocketHandler = websocketHandler;
    this.restHandler = restHandler;
  }

  /** 启动 IM 服务. */
  public void start() {
    var hap = HostAndPort.fromString(addr);
    this.disposableServer =
        HttpServer.create()
            .host(hap.getHost())
            .port(hap.getPort())
            .wiretap(Env.current() != Env.PROD)
            .tcpConfiguration(
                tcpServer -> {
                  var sb = tcpServer.configure();
                  sb.childHandler(
                          new IdleStateHandler(
                              IDLE_TIMEOUT_SECS, IDLE_TIMEOUT_SECS, IDLE_TIMEOUT_SECS))
                      .childHandler(new HttpExceptionHandler());
                  return tcpServer;
                })
            .route(
                routes -> {
                  // 注册 REST 服务
                  this.restHandler.registerService(routes);

                  var tokenResource = new TokenResource(null);
                  routes.get("/token", tokenResource::generateToken);
                  // Websocket 处理器
                  routes.ws(
                      IM_WS_PATH,
                      websocketHandler::handle,
                      WebsocketServerSpec.builder().handlePing(false).build());
                })
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
