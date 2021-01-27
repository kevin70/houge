package top.yein.tethys.rest;

import top.yein.tethys.core.AbstractApplicationIdentifier;
import top.yein.tethys.repository.ServerInstanceRepository;

/**
 * REST 应用程序标识接口的实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class RestApplicationIdentifier extends AbstractApplicationIdentifier {

  /**
   * 使用应用实例数据访问对象构造 REST 应用标识对象.
   *
   * @param serverInstanceRepository 应用实例数据访问对象
   */
  public RestApplicationIdentifier(ServerInstanceRepository serverInstanceRepository) {
    super(serverInstanceRepository);
  }

  @Override
  public String applicationName() {
    return "tethys-rest";
  }
}
