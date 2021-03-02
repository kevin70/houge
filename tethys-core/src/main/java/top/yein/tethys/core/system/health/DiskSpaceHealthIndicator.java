package top.yein.tethys.core.system.health;

import java.io.File;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.unit.DataSize;
import reactor.core.publisher.Mono;
import top.yein.tethys.system.health.Health;
import top.yein.tethys.system.health.HealthIndicator;

/**
 * 磁盘空间健康状况检查.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class DiskSpaceHealthIndicator implements HealthIndicator {

  private final File path;
  private final DataSize threshold;

  /**
   * 使用文件目录与剩余空间阈值创建实例.
   *
   * @param path 文件目录
   * @param threshold 阈值
   */
  public DiskSpaceHealthIndicator(File path, DataSize threshold) {
    this.path = path;
    this.threshold = threshold;
  }

  @Override
  public Mono<Health> health() {
    return Mono.fromSupplier(
        () -> {
          var builder = new Health.Builder();
          long diskFreeInBytes = this.path.getUsableSpace();
          if (diskFreeInBytes >= this.threshold.toBytes()) {
            builder.up();
          } else {
            log.warn("可用磁盘空间低于阈值. 可用: {} bytes (阈值: {})", diskFreeInBytes, this.threshold);
            builder.down();
          }
          return builder.withDetail("path", path).build();
        });
  }
}
