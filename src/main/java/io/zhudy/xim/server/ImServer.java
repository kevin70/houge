/**
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
package io.zhudy.xim.server;

import static io.zhudy.xim.ConfigKeys.IM_SERVER_ADDR;

import com.google.common.net.HostAndPort;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.WebsocketServerSpec;

@Log4j2
public class ImServer {

  public static final String VERSION_HTTP_PATH = "/version";
  public static final String IM_WS_PATH = "/im";

  private final ImSocketHandler imSocketHandler;
  private final HostAndPort hap;

  private DisposableServer disposableServer;

  @Inject
  public ImServer(@Named(IM_SERVER_ADDR) HostAndPort hap, ImSocketHandler imSocketHandler) {
    this.hap = hap;
    this.imSocketHandler = imSocketHandler;
  }

  /** 启动 IM 服务. */
  public void start() {
    this.disposableServer =
        HttpServer.create()
            .host(hap.getHost())
            .port(hap.getPort())
            .wiretap(true)
            .route(
                routes -> {
                  routes.get("/info", this::info);
                  // IM Socket 注册
                  routes.ws(
                      IM_WS_PATH,
                      imSocketHandler,
                      WebsocketServerSpec.builder().handlePing(true).build());
                })
            .bindNow();
    log.info("IM Server started at - {}", hap);
  }

  /** 停止 IM 服务. */
  public void stop() {
    if (disposableServer != null) {
      disposableServer.disposeNow();
    }
    log.info("IM Server stopped");
  }

  // FIXME 后面完善
  private Publisher<Void> info(HttpServerRequest request, HttpServerResponse response) {
    return response
        .header("content-type", "application/json")
        .sendHeaders()
        .sendString(Mono.just("{\"xim_version\": \"1.0.0\"}"));
  }
}
