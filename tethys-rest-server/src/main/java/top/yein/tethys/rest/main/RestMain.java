package top.yein.tethys.rest.main;

import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.yein.tethys.ApplicationIdentifier;
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
    var applicationContext = new ClassPathXmlApplicationContext("classpath*:spring.xml");
    applicationContext.start();

    // 启动 REST 服务
    final var applicationIdentifier = applicationContext.getBean(ApplicationIdentifier.class);
    final var restServer = applicationContext.getBean(RestServer.class);
    restServer.start();

    log.info("{} 服务启动成功 fid={}", applicationIdentifier.applicationName(), applicationIdentifier.fid());

    // 停止应用
    registerShutdownHook(
        () -> {
          log.info("REST 服务停止中...");
          // 停止操作
          restServer.stop();
          applicationContext.stop();
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
