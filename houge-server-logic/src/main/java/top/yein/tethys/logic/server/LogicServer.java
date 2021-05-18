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
package top.yein.tethys.logic.server;

import com.google.common.net.HostAndPort;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 逻辑服务.
 *
 * @author KK (kzou227@qq.com)
 */
public class LogicServer {

  private static final Logger log = LogManager.getLogger();

  private final LogicServerConfig serverConfig;
  private final List<BindableService> bindableServices;
  private Server server;

  /**
   * 使用服务配置与 gRPC服务列表构造对象.
   *
   * @param serverConfig 服务配置
   * @param bindableServices 绑定的 gRPC 服务
   */
  public LogicServer(LogicServerConfig serverConfig, List<BindableService> bindableServices) {
    this.serverConfig = serverConfig;
    this.bindableServices = bindableServices;
  }

  /** 启动逻辑服务. */
  public void start() {
    var hap = HostAndPort.fromString(serverConfig.getAddr());
    var address = new InetSocketAddress(hap.getHost(), hap.getPort());
    var builder = NettyServerBuilder.forAddress(address);
    for (BindableService service : bindableServices) {
      builder.addService(service);
    }

    this.server = builder.build();
    try {
      this.server.start();
      log.info("Logic gRPC服务启动成功 {}", this.server.getListenSockets());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /** 停止逻辑服务. */
  public void stop() {
    if (this.server != null) {
      if (this.server.isShutdown()) {
        log.warn("Logic gRPC服务已停止，正在重复停止Logic gRPC服务");
        return;
      }
      this.server.shutdownNow();
      log.info("Logic gRPC服务停止完成");
    }
  }
}
