package top.yein.tethys.rest.main;

import com.google.inject.Guice;
import com.google.inject.TypeLiteral;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.core.module.CoreModule;
import top.yein.tethys.storage.StorageModule;
import top.yein.tethys.rest.module.RestModule;
import top.yein.tethys.rest.server.RestServer;

/**
 * 主程序.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class RestMain implements Runnable {

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
        Guice.createInjector(new StorageModule(config), new CoreModule(config), new RestModule());
    // 应用程序监控
    var prometheusMeterRegistry = injector.getInstance(PrometheusMeterRegistry.class);
    Metrics.addRegistry(prometheusMeterRegistry);
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

    // 停止应用
    registerShutdownHook(
        () -> {
          log.info("REST 服务停止中...");
          // 停止操作
          restServer.stop();
          // 清理应用标识数据信息
          applicationIdentifier.clean();
          log.info("REST 服务停止成功");
        });
  }

  private Config loadConfig() {
    var config = ConfigFactory.parseResources("tethys.conf");
    log.info(
        "已加载的应用配置 \n=========================================================>>>\n{}<<<=========================================================",
        config.root().render());
    return config;
  }

  private void registerShutdownHook(final Runnable callback) {
    final var latch = new CountDownLatch(1);
    final Runnable r =
        () -> {
          try {
            callback.run();
          } catch (Exception e) {
            log.error("REST 服务停止失败", e);
          } finally {
            latch.countDown();
          }
        };
    Runtime.getRuntime().addShutdownHook(new Thread(r, "shutdown-hook"));

    try {
      latch.await();
    } catch (InterruptedException e) {
      log.warn("Interrupted!", e);
      Thread.currentThread().interrupt();
    }
  }
}
