package top.yein.tethys.rest;

import org.springframework.stereotype.Component;
import top.yein.tethys.core.AbstractApplicationIdentifier;
import top.yein.tethys.repository.ServerInstanceDao;

/**
 * REST 应用程序标识接口的实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Component
public class RestApplicationIdentifier extends AbstractApplicationIdentifier {

  /**
   * 使用应用实例数据访问对象构造 REST 应用标识对象.
   *
   * @param serverInstanceDao 应用实例数据访问对象
   */
  public RestApplicationIdentifier(ServerInstanceDao serverInstanceDao) {
    super(serverInstanceDao);
  }

  @Override
  public String applicationName() {
    return "tethys-rest";
  }
}
