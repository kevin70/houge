package top.yein.tethys.repository;

import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.entity.ServerInstance;
import top.yein.tethys.util.HostNameUtils;

/**
 * {@link ServerInstanceRepositoryImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class ServerInstanceRepositoryImplTest extends AbstractTestRepository {

  @Test
  void insert() throws UnknownHostException {
    var repo = new ServerInstanceRepositoryImpl(dc);

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(1);
    entity.setHostName(inetAddress.getHostName());
    entity.setHostAddress(inetAddress.getHostAddress());
    entity.setOsName(System.getProperty("os.name"));
    entity.setOsVersion(System.getProperty("os.version"));
    entity.setOsArch(System.getProperty("os.arch"));
    entity.setOsUser(System.getProperty("user.name"));
    entity.setJavaVmName(System.getProperty("java.vm.name"));
    entity.setJavaVmVersion(System.getProperty("java.vm.version"));
    entity.setJavaVmVendor(System.getProperty("java.vm.vendor"));
    entity.setWorkDir(System.getProperty("user.dir"));
    entity.setPid(ProcessHandle.current().pid());

    var p = transactional(repo.insert(entity));
    StepVerifier.create(p).expectNextCount(1).verifyComplete();
  }

  @Test
  void update() {}

  @Test
  void updateCheckTime() {}

  @Test
  void findById() {}
}
