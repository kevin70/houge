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
   * @param builder 应用信息构建器.
   */
  Mono<Void> contribute(Info.Builder builder);
}
