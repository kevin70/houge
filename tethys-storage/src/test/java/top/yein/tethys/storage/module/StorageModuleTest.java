package top.yein.tethys.storage.module;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;
import top.yein.tethys.storage.ServerInstanceDao;
import top.yein.tethys.storage.ServerInstanceDaoImpl;

/**
 * {@link StorageModule} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class StorageModuleTest {

  @Test
  void execute() {
    var config = ConfigFactory.parseResources("tethys-test.conf");
    var injector = Guice.createInjector(new StorageModule(config));

    assertThat(injector.getInstance(ServerInstanceDao.class))
        .isInstanceOf(ServerInstanceDaoImpl.class);
  }
}
