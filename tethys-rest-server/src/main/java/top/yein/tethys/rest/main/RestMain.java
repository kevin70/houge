package top.yein.tethys.rest.main;

import com.google.inject.Guice;
import com.google.inject.Stage;
import com.typesafe.config.ConfigFactory;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import top.yein.tethys.core.Env;
import top.yein.tethys.rest.server.RestServer;

/**
 * 主程序.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class RestMain implements Runnable {

  private final String CONFIG_FILE = "tethys.conf";

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
    final var env = Env.current();
    final var stage = env == Env.PROD ? Stage.PRODUCTION : Stage.DEVELOPMENT;
    log.info("正在启动 REST Server, 当前环境参数 [env={}, stage={}]", env, stage);

    // 加载应用配置
    var config = ConfigFactory.parseResources(CONFIG_FILE).resolve();
    log.info(
        "------------------------------ CONFIG ------------------------------{}{}",
        System.lineSeparator(),
        config.root().render());

    // 初始化 Guice
    log.info("初始化 Guice");
    final var injector = Guice.createInjector(stage, new RestGuiceModule(config));
    log.info("初始化 Guice 成功");

    // 启动 REST 服务
    final var restServer = injector.getInstance(RestServer.class);
    restServer.start();

    log.info("REST 服务启动成功");

    // 停止应用
    registerShutdownHook(
        () -> {
          log.info("REST 服务停止中...");
          // 停止操作
          restServer.stop();
          log.info("REST 服务停止成功");
        });
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
