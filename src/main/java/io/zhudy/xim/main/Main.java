package io.zhudy.xim.main;

import com.google.inject.Guice;
import com.google.inject.Stage;
import io.zhudy.xim.Env;
import io.zhudy.xim.server.ImServer;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Hooks;

/**
 * XIM 应用程序入口.
 *
 * @auth Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class Main implements Runnable {

  @Override
  public void run() {
    Hooks.onOperatorDebug();

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
    final var t =
        new Thread(
            () -> {
              callback.run();
              latch.countDown();
            },
            "xim-shutdown");
    Runtime.getRuntime().addShutdownHook(t);

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    new Main().run();
  }
}
