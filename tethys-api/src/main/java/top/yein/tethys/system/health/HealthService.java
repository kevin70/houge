package top.yein.tethys.system.health;

import reactor.core.publisher.Mono;

/**
 * 运行健康状况服务接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface HealthService {

  /**
   * 返回运行健康状况.
   *
   * @param includeDetails 是否应包括或删除详细信息
   * @return 运行健康状况
   */
  Mono<HealthComposite> health(boolean includeDetails);
}
