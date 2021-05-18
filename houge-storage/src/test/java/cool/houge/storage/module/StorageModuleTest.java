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
package cool.houge.storage.module;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;
import cool.houge.storage.ServerInstanceDao;
import cool.houge.storage.impl.ServerInstanceDaoImpl;

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
