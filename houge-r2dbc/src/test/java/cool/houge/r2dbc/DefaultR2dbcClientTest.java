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
package cool.houge.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * {@link DefaultR2dbcClient} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class DefaultR2dbcClientTest {

  @Test
  void getConnectionFactory() {
    var connectionFactory = Mockito.mock(ConnectionFactory.class);
    var rc = new DefaultR2dbcClient(connectionFactory);
    assertThat(rc.getConnectionFactory()).isEqualTo(connectionFactory);
  }

  @Test
  void sql() {
    var connectionFactory = Mockito.mock(ConnectionFactory.class);
    var rc = new DefaultR2dbcClient(connectionFactory);
    var spec = rc.sql("select version()");
    assertThat(spec).isNotNull();
  }
}
