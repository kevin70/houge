package top.yein.tethys.im;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import top.yein.tethys.core.AbstractApplicationIdentifier;
import top.yein.tethys.repository.ServerInstanceDao;

/**
 * 应用程序标识符接口.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
@Component
public class ImApplicationIdentifier extends AbstractApplicationIdentifier {

  /**
   * 构造函数.
   *
   * @param serverInstanceDao 应用实例数据访问对象
   */
  public ImApplicationIdentifier(ServerInstanceDao serverInstanceDao) {
    super(serverInstanceDao);
  }

  @Override
  public String applicationName() {
    return "tethys-im";
  }
}
