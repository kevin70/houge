package top.yein.tethys.core.system.health;

import java.io.File;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.system.health.HealthStatus;

/**
 * {@link DiskSpaceHealthIndicator} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class DiskSpaceHealthIndicatorTest {

  @Test
  void health() {
    var path = new File("").getAbsoluteFile();
    var indicator = new DiskSpaceHealthIndicator(path, 1);
    var p = indicator.health();
    StepVerifier.create(p)
        .expectNextMatches(health -> health.getStatus() == HealthStatus.UP)
        .expectComplete()
        .verify();
  }

  @Test
  void healthDown() {
    var path = new File("").getAbsoluteFile();
    var indicator = new DiskSpaceHealthIndicator(path, path.getTotalSpace());
    var p = indicator.health();
    StepVerifier.create(p)
        .expectNextMatches(health -> health.getStatus() == HealthStatus.DOWN)
        .expectComplete()
        .verify();
  }
}
