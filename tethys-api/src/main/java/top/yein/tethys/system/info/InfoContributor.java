package top.yein.tethys.system.info;

import reactor.core.publisher.Mono;

/**
 * 提供其他信息详细信息.
 *
 * @author KK (kzou227@qq.com)
 */
@FunctionalInterface
public interface InfoContributor {

  /**
   * 返回应用信息.
   *
   * @return 应用信息
   */
  Mono<Info> contribute();
}
