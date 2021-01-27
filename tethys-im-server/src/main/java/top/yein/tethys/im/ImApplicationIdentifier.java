package top.yein.tethys.im;

import lombok.extern.log4j.Log4j2;
import top.yein.tethys.core.AbstractApplicationIdentifier;
import top.yein.tethys.repository.ServerInstanceRepository;

/**
 * 应用程序标识符接口.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class ImApplicationIdentifier extends AbstractApplicationIdentifier {

  /**
   * 构造函数.
   *
   * @param serverInstanceRepository 应用实例数据访问对象
   */
  public ImApplicationIdentifier(ServerInstanceRepository serverInstanceRepository) {
    super(serverInstanceRepository);
  }

  @Override
  public String applicationName() {
    return "tethys-im";
  }
}
