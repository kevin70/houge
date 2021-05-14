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
package top.yein.tethys.ws.server;

import com.google.common.net.HostAndPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.netty.http.server.WebsocketServerSpec;

/**
 * WebSocket服务.
 *
 * @author KK (kzou227@qq.com)
 */
public class WsServer {

  private static final Logger log = LogManager.getLogger();
  private final WsServerConfig serverConfig;
  private final WebSocketHandler webSocketHandler;

  private DisposableServer disposableServer;

  /**
   * 使用服务配置与消息处理器构造对象.
   *
   * @param serverConfig 服务配置
   * @param webSocketHandler WebSocket消息处理器
   */
  public WsServer(WsServerConfig serverConfig, WebSocketHandler webSocketHandler) {
    this.serverConfig = serverConfig;
    this.webSocketHandler = webSocketHandler;
  }

  /**
   * 启动WebSocket服务.
   *
   * <p>服务启动完成后可使用WebSocket连接.
   */
  public void start() {
    log.debug("正在启动WS服务 addr={}", serverConfig.getAddr());
    var hap = HostAndPort.fromString(serverConfig.getAddr());
    var routes = HttpServerRoutes.newRoutes();

    routes.ws(
        "/ws", webSocketHandler::handle, WebsocketServerSpec.builder().handlePing(false).build());

    this.disposableServer =
        HttpServer.create()
            .host(hap.getHost())
            .port(hap.getPort())
            .wiretap(true)
            .handle(routes)
            .bindNow();
    log.info("WS服务启动成功 [{}]", disposableServer.address());
  }

  /**
   * 停止WebSocket服务.
   *
   * <p>释放WS资源.
   */
  public void stop() {
    if (this.disposableServer != null) {
      var address = disposableServer.address();
      this.disposableServer.disposeNow();
      log.info("WS服务停止成功 [{}]", address);
    }
  }
}
