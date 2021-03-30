/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.yein.tethys.storage;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.github.javafaker.Faker;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.entity.ServerInstance;
import top.yein.tethys.util.HostNameUtils;

/**
 * {@link ServerInstanceDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class ServerInstanceDaoImplTest extends AbstractTestDao {

  private final Faker faker = new Faker();

  private ServerInstanceDao newServerInstanceDao() {
    return new ServerInstanceDaoImpl(r2dbcClient);
  }

  @Test
  void insert() throws UnknownHostException {
    var dao = newServerInstanceDao();

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(faker.random().nextInt(700000, 777777));
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

    var p = dao.insert(entity);
    StepVerifier.create(p).expectComplete().verify();

    StepVerifier.create(
            r2dbcClient
                .sql("select * from server_instances where id=$1")
                .bind(0, entity.getId())
                .fetch()
                .one())
        .consumeNextWith(
            dbRow ->
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
                      s.assertThat(dbRow.get("os_name"))
                          .as("os_name")
                          .isEqualTo(entity.getOsName());
                      s.assertThat(dbRow.get("os_version"))
                          .as("os_version")
                          .isEqualTo(entity.getOsVersion());
                      s.assertThat(dbRow.get("os_arch"))
                          .as("os_arch")
                          .isEqualTo(entity.getOsArch());
                      s.assertThat(dbRow.get("os_user"))
                          .as("os_user")
                          .isEqualTo(entity.getOsUser());
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
                    }));

    // 清理数据
    clean("delete from server_instances where id=$1", new Object[] {entity.getId()});
  }

  @Test
  void delete() throws UnknownHostException {
    var dao = newServerInstanceDao();

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(faker.random().nextInt(700000, 777777));
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

    var p = dao.insert(entity).then(dao.delete(entity.getId()));
    StepVerifier.create(p).expectComplete().verify();
  }

  @Test
  void update() throws UnknownHostException {
    var dao = newServerInstanceDao();

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(faker.random().nextInt(700000, 777777));
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

    var p = dao.insert(entity).then(dao.update(newEntity));
    StepVerifier.create(p).expectComplete().verify();

    StepVerifier.create(
            r2dbcClient
                .sql("select * from server_instances where id=$1")
                .bind(0, entity.getId())
                .fetch()
                .one())
        .consumeNextWith(
            dbRow ->
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
                    }))
        .expectComplete()
        .verify();

    // 清理数据
    clean("delete from server_instances where id=$1", new Object[] {entity.getId()});
  }

  @Test
  void updateCheckTime() throws UnknownHostException {
    var dao = newServerInstanceDao();

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(faker.random().nextInt(700000, 777777));
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

    var p = dao.insert(entity).then(dao.updateCheckTime(entity.getId()));
    StepVerifier.create(p).expectComplete().verify();

    // 清理数据
    clean("delete from server_instances where id=$1", new Object[] {entity.getId()});
  }

  @Test
  void findById() throws UnknownHostException {
    var dao = newServerInstanceDao();

    var inetAddress = HostNameUtils.getLocalHostLANAddress();
    var entity = new ServerInstance();
    entity.setId(faker.random().nextInt(700000, 777777));
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

    var p = dao.insert(entity).then(dao.findById(entity.getId()));
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

    // 清理数据
    clean("delete from server_instances where id=$1", new Object[] {entity.getId()});
  }
}
