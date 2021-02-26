package top.yein.tethys.system.health;

import reactor.core.publisher.Mono;

/**
 * 策略接口, 用于提供服务运行状况.
 *
 * @author KK (kzou227@qq.com)
 */
@FunctionalInterface
public interface HealthIndicator {

  /**
   * 返回运行健康状况.
   *
   * @param includeDetails 是否应包括或删除详细信息
   * @return 运行健康状况
   */
  default Mono<Health> getHealth(boolean includeDetails) {
    return health().map(h -> includeDetails ? h : h.withoutDetails());
  }

  /**
   * 返回运行健康状况.
   *
   * @return 运行健康状况
   */
  Mono<Health> health();
}
