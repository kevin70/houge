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
package top.yein.tethys.im.server;

import com.google.common.net.HostAndPort;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 * gRPC 服务启动器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class GrpcServer {

  private final List<BindableService> services;
  private final HostAndPort hap;
  private Server server;

  /**
   * @param addr
   * @param services
   */
  public GrpcServer(String addr, List<BindableService> services) {
    this.hap = HostAndPort.fromString(addr);
    this.services = services;
  }

  /** */
  public void start() {
    var serverBuilder =
        NettyServerBuilder.forAddress(new InetSocketAddress(hap.getHost(), hap.getPort()));
    for (BindableService service : services) {
      serverBuilder.addService(service);
      log.info("gRPC 添加服务 {}", service);
    }

    this.server = serverBuilder.build();
    try {
      this.server.start();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    log.info("gRPC 服务 {} 启动成功", hap);
  }

  /** */
  public void stop() {
    if (server != null) {
      server.shutdownNow();
    }
  }
}
