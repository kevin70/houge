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
package top.yein.tethys.system.health;

import java.io.File;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/**
 * 磁盘空间健康状况检查.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class DiskSpaceHealthIndicator implements HealthIndicator {

  private final String componentName;
  private final File path;
  private final long threshold;

  /**
   * 使用文件目录与剩余空间阈值创建实例.
   *
   * @param path 文件目录
   * @param threshold 阈值
   */
  public DiskSpaceHealthIndicator(File path, long threshold) {
    this("DiskSpace", path, threshold);
  }

  /**
   * 使用组件名称文件目录与剩余空间阈值创建实例.
   *
   * @param componentName 组件名称
   * @param path 文件目录
   * @param threshold 阈值
   */
  public DiskSpaceHealthIndicator(String componentName, File path, long threshold) {
    Objects.requireNonNull(componentName, "[componentName]不能为null");
    this.componentName = componentName;
    this.path = path;
    this.threshold = threshold;
  }

  @Override
  public Mono<Health> health() {
    return Mono.fromSupplier(
        () -> {
          var builder = new Health.Builder(componentName);
          long diskFreeInBytes = this.path.getUsableSpace();
          if (diskFreeInBytes >= this.threshold) {
            builder.up();
          } else {
            log.warn("可用磁盘空间低于阈值. 可用: {} bytes (阈值: {})", diskFreeInBytes, this.threshold);
            builder.down();
          }
          return builder.withDetail("path", path).build();
        });
  }
}
