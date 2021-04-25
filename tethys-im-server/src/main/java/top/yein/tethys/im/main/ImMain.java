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
package top.yein.tethys.im.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.grpc.BindableService;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.core.module.CoreModule;
import top.yein.tethys.grpc.service.module.GrpcServiceModule;
import top.yein.tethys.im.cluster.PlainClusterManager;
import top.yein.tethys.im.module.ImModule;
import top.yein.tethys.im.server.GrpcServer;
import top.yein.tethys.im.server.ImServer;
import top.yein.tethys.im.server.WebsocketHandler;
import top.yein.tethys.service.module.ServiceModule;
import top.yein.tethys.storage.module.StorageModule;
import top.yein.tethys.util.AppShutdownHelper;

/**
 * 主程序.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class ImMain implements Runnable {

  private final AppShutdownHelper shutdownHelper = new AppShutdownHelper();

  /**
   * 程序入口.
   *
   * @param args 启动参数
   */
  public static void main(String[] args) {
    new ImMain().run();
  }

  @Override
  public void run() {
    // 初始化配置
    final var config = loadConfig();

    // 初始化 Guice
    final var injector =
        Guice.createInjector(
            new StorageModule(config),
            new ServiceModule(config),
            new GrpcServiceModule(),
            new CoreModule(config),
            new ImModule(config));
    // 初始化应用程序监控
    this.initMetrics(injector);
    // 初始化IM服务
    this.initImServer(injector, config);
    // 初始化 gRPC 服务
    this.initGrpcServer(injector, config);
    // 初始化集群
    this.initCluster(injector, config);

    // 初始化应用基础信息
    var applicationIdentifier = injector.getInstance(ApplicationIdentifier.class);
    log.info(
        "{} 服务启动成功 fid={}", applicationIdentifier.applicationName(), applicationIdentifier.fid());

    // 应用停止
    shutdownHelper.addCallback(
        () -> {
          log.info("清理应用程序标识");
          applicationIdentifier.clean();
        });
    shutdownHelper.run();
    log.info("IM 优雅停止完成...");
  }

  private void initMetrics(Injector injector) {
    var prometheusMeterRegistry = injector.getInstance(PrometheusMeterRegistry.class);
    Metrics.addRegistry(prometheusMeterRegistry);
  }

  private void initImServer(Injector injector, Config config) {
    // 启动 IM 服务
    var imServer =
        new ImServer(
            config.getString(ConfigKeys.IM_SERVER_ADDR),
            injector.getInstance(WebsocketHandler.class),
            injector.getInstance(Interceptors.class),
            injector.findBindingsByType(TypeLiteral.get(RoutingService.class)).stream()
                .map(b -> b.getProvider().get())
                .collect(Collectors.toList()));
    imServer.start();

    this.shutdownHelper.addCallback(
        () -> {
          log.info("停止 IM Server");
          imServer.stop();
          log.info("停止 IM Server 完成");
        });
  }

  private void initGrpcServer(Injector injector, Config config) {
    var grpcServer =
        new GrpcServer(
            config.getString(ConfigKeys.GRPC_SERVER_ADDR),
            injector.findBindingsByType(TypeLiteral.get(BindableService.class)).stream()
                .map(b -> b.getProvider().get())
                .collect(Collectors.toList()));
    grpcServer.start();

    this.shutdownHelper.addCallback(
        () -> {
          log.info("停止 gRPC Server");
          grpcServer.stop();
          log.info("停止 gRPC Server 完成");
        });
  }

  private void initCluster(Injector injector, Config config) {
    var enabled =
        config.hasPath(ConfigKeys.CLUSTER_ENABLED) && config.getBoolean(ConfigKeys.CLUSTER_ENABLED);
    if (!enabled) {
      log.debug("IM未开启集群开关");
      return;
    }
    var clusterManager = injector.getInstance(PlainClusterManager.class);
    log.info("IM集群初始化成功");
    shutdownHelper.addCallback(
        () -> {
          clusterManager.close();
          log.info("集群清理完成");
        });
  }

  private Config loadConfig() {
    var config = ConfigFactory.parseResources("tethys.conf");
    log.info(
        "已加载的应用配置 \n=========================================================>>>\n{}<<<=========================================================",
        config.root().render());
    return config;
  }
}
