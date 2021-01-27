package top.yein.tethys.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.net.UnknownHostException;
import java.util.Map;
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

  private ServerInstanceRepository newServerInstanceRepository() {
    return new ServerInstanceRepositoryImpl(dc);
  }

  @Test
  void insert() throws UnknownHostException {
    var repo = newServerInstanceRepository();

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(1);
    entity.setAppName("junit-test");
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

    var fo = findOne("select * from t_server_instance where id=:id", Map.of("id", entity.getId()));
    var p = transactional(repo.insert(entity).zipWith(fo));
    StepVerifier.create(p)
        .consumeNextWith(
            tuple -> {
              assertThat(tuple.getT1()).isEqualTo(1);
              var dbRow = tuple.getT2();

              assertSoftly(
                  s -> {
                    s.assertThat(dbRow.get("id")).as("id").isEqualTo(entity.getId());
                    s.assertThat(dbRow.get("app_name"))
                        .as("app_name")
                        .isEqualTo(entity.getAppName());
                    s.assertThat(dbRow.get("host_name"))
                        .as("host_name")
                        .isEqualTo(entity.getHostName());
                    s.assertThat(dbRow.get("host_address"))
                        .as("host_address")
                        .isEqualTo(entity.getHostAddress());
                    s.assertThat(dbRow.get("os_name")).as("os_name").isEqualTo(entity.getOsName());
                    s.assertThat(dbRow.get("os_version"))
                        .as("os_version")
                        .isEqualTo(entity.getOsVersion());
                    s.assertThat(dbRow.get("os_arch")).as("os_arch").isEqualTo(entity.getOsArch());
                    s.assertThat(dbRow.get("os_user")).as("os_user").isEqualTo(entity.getOsUser());
                    s.assertThat(dbRow.get("java_vm_name"))
                        .as("java_vm_name")
                        .isEqualTo(entity.getJavaVmName());
                    s.assertThat(dbRow.get("java_vm_version"))
                        .as("java_vm_version")
                        .isEqualTo(entity.getJavaVmVersion());
                    s.assertThat(dbRow.get("java_vm_vendor"))
                        .as("java_vm_vendor")
                        .isEqualTo(entity.getJavaVmVendor());
                    s.assertThat(dbRow.get("work_dir"))
                        .as("work_dir")
                        .isEqualTo(entity.getWorkDir());
                    s.assertThat(dbRow.get("pid")).as("pid").isEqualTo(entity.getPid());
                    s.assertThat(dbRow.get("ver")).as("ver").isEqualTo(1);
                    s.assertThat(dbRow.get("create_time")).as("create_time").isNotNull();
                    s.assertThat(dbRow.get("check_time")).as("check_time").isNotNull();
                  });
            })
        .expectComplete()
        .verify();
  }

  @Test
  void update() throws UnknownHostException {
    var repo = newServerInstanceRepository();

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(1);
    entity.setAppName("junit-test");
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

    var newEntity = new ServerInstance();
    newEntity.setId(entity.getId());
    newEntity.setAppName(entity.getAppName() + ".new");
    newEntity.setHostName(entity.getHostName() + ".new");
    newEntity.setHostAddress(entity.getHostAddress() + ".new");
    newEntity.setOsName(entity.getOsName() + ".new");
    newEntity.setOsVersion(entity.getOsVersion() + ".new");
    newEntity.setOsArch(entity.getOsVersion() + ".new");
    newEntity.setOsUser(entity.getOsUser() + ".new");
    newEntity.setJavaVmName(entity.getJavaVmName() + ".new");
    newEntity.setJavaVmVersion(entity.getJavaVmVersion() + ".new");
    newEntity.setJavaVmVendor(entity.getJavaVmVendor() + ".new");
    newEntity.setWorkDir(entity.getWorkDir() + ".new");
    newEntity.setPid(entity.getPid() + 1);
    newEntity.setVer(1);

    var fo = findOne("select * from t_server_instance where id=:id", Map.of("id", entity.getId()));
    var p = transactional(repo.insert(entity).then(repo.update(newEntity).zipWith(fo)));
    StepVerifier.create(p)
        .consumeNextWith(
            tuple -> {
              assertThat(tuple.getT1()).isEqualTo(1);
              var dbRow = tuple.getT2();

              assertSoftly(
                  s -> {
                    s.assertThat(dbRow.get("id")).as("id").isEqualTo(newEntity.getId());
                    s.assertThat(dbRow.get("app_name"))
                        .as("app_name")
                        .isEqualTo(newEntity.getAppName());
                    s.assertThat(dbRow.get("host_name"))
                        .as("host_name")
                        .isEqualTo(newEntity.getHostName());
                    s.assertThat(dbRow.get("host_address"))
                        .as("host_address")
                        .isEqualTo(newEntity.getHostAddress());
                    s.assertThat(dbRow.get("os_name"))
                        .as("os_name")
                        .isEqualTo(newEntity.getOsName());
                    s.assertThat(dbRow.get("os_version"))
                        .as("os_version")
                        .isEqualTo(newEntity.getOsVersion());
                    s.assertThat(dbRow.get("os_arch"))
                        .as("os_arch")
                        .isEqualTo(newEntity.getOsArch());
                    s.assertThat(dbRow.get("os_user"))
                        .as("os_user")
                        .isEqualTo(newEntity.getOsUser());
                    s.assertThat(dbRow.get("java_vm_name"))
                        .as("java_vm_name")
                        .isEqualTo(newEntity.getJavaVmName());
                    s.assertThat(dbRow.get("java_vm_version"))
                        .as("java_vm_version")
                        .isEqualTo(newEntity.getJavaVmVersion());
                    s.assertThat(dbRow.get("java_vm_vendor"))
                        .as("java_vm_vendor")
                        .isEqualTo(newEntity.getJavaVmVendor());
                    s.assertThat(dbRow.get("work_dir"))
                        .as("work_dir")
                        .isEqualTo(newEntity.getWorkDir());
                    s.assertThat(dbRow.get("pid")).as("pid").isEqualTo(newEntity.getPid());
                    s.assertThat(dbRow.get("ver")).as("ver").isEqualTo(newEntity.getVer() + 1);
                    s.assertThat(dbRow.get("create_time")).as("create_time").isNotNull();
                    s.assertThat(dbRow.get("check_time")).as("check_time").isNotNull();
                  });
            })
        .expectComplete()
        .verify();
  }

  @Test
  void updateCheckTime() throws UnknownHostException {
    var repo = newServerInstanceRepository();

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(1);
    entity.setAppName("junit-test");
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

    var p = transactional(repo.insert(entity).then(repo.updateCheckTime(entity.getId())));
    StepVerifier.create(p).expectNext(1).expectComplete().verify();
  }

  @Test
  void findById() throws UnknownHostException {
    var repo = newServerInstanceRepository();

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(1);
    entity.setAppName("junit-test");
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

    var p = transactional(repo.insert(entity).then(repo.findById(entity.getId())));
    StepVerifier.create(p)
        .consumeNextWith(
            dbRow ->
                assertSoftly(
                    s -> {
                      s.assertThat(dbRow.getId()).as("id").isEqualTo(entity.getId());
                      s.assertThat(dbRow.getAppName())
                          .as("app_name")
                          .isEqualTo(entity.getAppName());
                      s.assertThat(dbRow.getHostName())
                          .as("host_name")
                          .isEqualTo(entity.getHostName());
                      s.assertThat(dbRow.getHostAddress())
                          .as("host_address")
                          .isEqualTo(entity.getHostAddress());
                      s.assertThat(dbRow.getOsName()).as("os_name").isEqualTo(entity.getOsName());
                      s.assertThat(dbRow.getOsVersion())
                          .as("os_version")
                          .isEqualTo(entity.getOsVersion());
                      s.assertThat(dbRow.getOsArch()).as("os_arch").isEqualTo(entity.getOsArch());
                      s.assertThat(dbRow.getOsUser()).as("os_user").isEqualTo(entity.getOsUser());
                      s.assertThat(dbRow.getJavaVmName())
                          .as("java_vm_name")
                          .isEqualTo(entity.getJavaVmName());
                      s.assertThat(dbRow.getJavaVmVersion())
                          .as("java_vm_version")
                          .isEqualTo(entity.getJavaVmVersion());
                      s.assertThat(dbRow.getJavaVmVendor())
                          .as("java_vm_vendor")
                          .isEqualTo(entity.getJavaVmVendor());
                      s.assertThat(dbRow.getWorkDir())
                          .as("work_dir")
                          .isEqualTo(entity.getWorkDir());
                      s.assertThat(dbRow.getPid()).as("pid").isEqualTo(entity.getPid());
                      s.assertThat(dbRow.getVer()).as("ver").isEqualTo(1);
                      s.assertThat(dbRow.getCreateTime()).as("create_time").isNotNull();
                      s.assertThat(dbRow.getCheckTime()).as("check_time").isNotNull();
                    }))
        .expectComplete()
        .verify();
  }
}
