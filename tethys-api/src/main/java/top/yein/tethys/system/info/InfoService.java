package top.yein.tethys.system.info;

import reactor.core.publisher.Mono;

/**
 * 应用程序系统信息服务接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface InfoService {

  /**
   * 返回应用程序信息.
   *
   * @return
   */
  Mono<Info> info();
}
