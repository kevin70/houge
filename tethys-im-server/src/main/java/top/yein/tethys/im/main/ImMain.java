package top.yein.tethys.im.main;

import com.google.inject.Guice;
import com.google.inject.Stage;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import top.yein.tethys.core.Env;

/**
 * @author KK (kzou227@qq.com)
 * @date 2020-12-28 11:20
 */
@Log4j2
public class ImMain implements Runnable {

  public static void main(String[] args) {
    new ImMain().run();
  }

  @Override
  public void run() {
    final var env = Env.current();
    final var stage = env == Env.PROD ? Stage.PRODUCTION : Stage.DEVELOPMENT;
    log.info("正在启动 XIM 服务, 当前 [env={}, stage={}]", env, stage);

    // 初始化 Guice
    log.info("初始化 Guice");
    final var injector = Guice.createInjector(stage, new ConfigModule(), new ImModule());
    log.info("初始化 Guice 成功");

    // 启动 IM 服务
    final var imServer = injector.getInstance(ImServer.class);
    imServer.start();

    log.info("XIM 服务启动成功");

    // 停止应用
    registerShutdownHook(
        () -> {
          log.info("XIM 服务停止中...");
          // 停止操作
          imServer.stop();
          log.info("XIM 服务停止成功");
        });
  }

  private void registerShutdownHook(final Runnable callback) {
    final var latch = new CountDownLatch(1);
    final Runnable r =
        () -> {
          try {
            callback.run();
          } catch (Exception e) {
            log.error("XIM 服务停止失败", e);
          } finally {
            latch.countDown();
          }
        };
    Runtime.getRuntime().addShutdownHook(new Thread(r, "xim-shutdown"));

    try {
      latch.await();
    } catch (InterruptedException e) {
      log.warn("Interrupted!", e);
      Thread.currentThread().interrupt();
    }
  }
}
