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
package cool.houge.rest.main;

import com.google.inject.Guice;
import com.google.inject.TypeLiteral;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cool.houge.ConfigKeys;
import cool.houge.rest.controller.Interceptors;
import cool.houge.rest.controller.RoutingService;
import cool.houge.rest.module.RestModule;
import cool.houge.rest.server.RestServer;
import cool.houge.service.module.GrpcServiceModule;
import cool.houge.service.module.ServiceModule;
import cool.houge.storage.module.StorageModule;
import cool.houge.system.identifier.ApplicationIdentifier;
import cool.houge.util.AppShutdownHelper;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 主程序.
 *
 * @author KK (kzou227@qq.com)
 */
public class RestMain implements Runnable {

  private static final Logger log = LogManager.getLogger();
  private static final String CONFIG_FILE = "houge-rest.conf";
  private final AppShutdownHelper shutdownHelper = new AppShutdownHelper();

  /**
   * 程序入口.
   *
   * @param args 启动参数
   */
  public static void main(String[] args) {
    new RestMain().run();
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
            new GrpcServiceModule(config),
            new RestModule(config));

    // 启动 IM 服务
    var applicationIdentifier = injector.getInstance(ApplicationIdentifier.class);
    var restServer =
        new RestServer(
            config.getString(ConfigKeys.REST_SERVER_ADDR),
            injector.getInstance(Interceptors.class),
            injector.findBindingsByType(TypeLiteral.get(RoutingService.class)).stream()
                .map(b -> b.getProvider().get())
                .collect(Collectors.toList()));

    restServer.start();
    log.info(
        "{} 服务启动成功 fid={}", applicationIdentifier.applicationName(), applicationIdentifier.fid());

    shutdownHelper
        .addCallback(restServer::stop)
        // 清理应用标识数据信息
        .addCallback(applicationIdentifier::clean)
        .run();
    log.info("REST 服务停止完成");
  }

  private Config loadConfig() {
    var config = ConfigFactory.parseResources(CONFIG_FILE).resolve();
    log.info(
        "已加载的应用配置 \n=========================================================>>>\n{}<<<=========================================================",
        config.root().render());
    return config;
  }
}
