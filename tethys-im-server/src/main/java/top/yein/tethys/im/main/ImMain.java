package top.yein.tethys.im.main;

import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.im.server.ImServer;

/**
 * 主程序.
 *
 * @author KK (kzou227@qq.com)
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
    var applicationContext = new ClassPathXmlApplicationContext("classpath*:spring.xml");
    applicationContext.start();

    // 启动 IM 服务
    final var applicationIdentifier = applicationContext.getBean(ApplicationIdentifier.class);
    final var imServer = applicationContext.getBean(ImServer.class);
    imServer.start();

    log.info(
        "{} 服务启动成功 fid={}", applicationIdentifier.applicationName(), applicationIdentifier.fid());

    // 停止应用
    registerShutdownHook(
        () -> {
          log.info("IM 服务停止中...");
          // 停止操作
          imServer.stop();
          // 清理应用标识数据信息
          applicationIdentifier.clean();
          // 停止 spring 应用上下文
          applicationContext.stop();
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
