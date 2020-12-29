package top.yein.tethys.im.main;

import com.google.inject.Guice;
import com.google.inject.Stage;
import com.typesafe.config.ConfigFactory;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import top.yein.tethys.core.Env;
import top.yein.tethys.im.server.ImServer;

/**
 * 主程序.
 *
 * @author KK (kzou227@qq.com)
 * @date 2020-12-28 11:20
 */
@Log4j2
public class ImMain implements Runnable {

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
    final var env = Env.current();
    final var stage = env == Env.PROD ? Stage.PRODUCTION : Stage.DEVELOPMENT;
    log.info("正在启动 IM Server, 当前环境参数 [env={}, stage={}]", env, stage);

    //
    var config = ConfigFactory.parseResources("tethys.conf").resolve();
    log.info(
        "------------------------------ CONFIG ------------------------------\n{}\n",
        config.root().render());

    // 初始化 Guice
    log.info("初始化 Guice");
    final var injector = Guice.createInjector(stage, new GuiceModule(config));
    log.info("初始化 Guice 成功");

    // 启动 IM 服务
    final var imServer = injector.getInstance(ImServer.class);
    imServer.start();

    log.info("IM 服务启动成功");

    // 停止应用
    registerShutdownHook(
        () -> {
          log.info("IM 服务停止中...");
          // 停止操作
          imServer.stop();
          log.info("IM 服务停止成功");
        });
  }

  private void registerShutdownHook(final Runnable callback) {
    final var latch = new CountDownLatch(1);
    final Runnable r =
        () -> {
          try {
            callback.run();
          } catch (Exception e) {
            log.error("IM 服务停止失败", e);
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
