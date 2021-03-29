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
package top.yein.tethys.core.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import top.yein.tethys.r2dbc.ConnectionAccessor;
import top.yein.tethys.r2dbc.Parameter;

/**
 * {@link DefaultExecuteSpec} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class DefaultExecuteSpecTest {

  //  @Test
  //  void fetch() {
  //    var connectionFactory =
  //        ConnectionFactories.get(
  //            "r2dbc:postgresql://postgres:hellohuixin@192.168.1.106:5432/tethys");
  //    var sql = "select * from names";
  //    var spec = new DefaultExecuteSpec(connectionFactory, sql);
  //    var p = spec.fetch().one();
  //    StepVerifier.create(p)
  //        .consumeNextWith(
  //            map -> {
  //              System.out.println(map);
  //            })
  //        .expectComplete()
  //        .verify();
  //  }
  //
  //  @Test
  //  void rowsUpdated() {
  //    var connectionFactory =
  //        ConnectionFactories.get(
  //            "r2dbc:postgresql://postgres:hellohuixin@192.168.1.106:5432/tethys");
  //    var sql = "insert into names(name) values($1)";
  //    var spec = new DefaultExecuteSpec(connectionFactory, sql);
  //    var p = spec.bind(0, "kk").rowsUpdated();
  //    StepVerifier.create(p).expectNext(1).expectComplete().verify();
  //  }

  @Test
  void bind() {
    var connectionAccessor = mock(ConnectionAccessor.class);
    var spec = new DefaultExecuteSpec(connectionAccessor, "select version()");

    // 绑定单项值
    spec.bind(0, "hello");
    spec.bind(1, null, Long.class);
    spec.bind(2, Parameter.from("world"));
    spec.bind(3, Parameter.fromOrNull(null, Integer.class));
    var parameters = (Map<Integer, Parameter>) Whitebox.getInternalState(spec, "parameters");
    assertThat(parameters.get(0))
        .hasFieldOrPropertyWithValue("value", "hello")
        .hasFieldOrPropertyWithValue("type", String.class);
    assertThat(parameters.get(1))
        .hasFieldOrPropertyWithValue("value", null)
        .hasFieldOrPropertyWithValue("type", Long.class);
    assertThat(parameters.get(2))
        .hasFieldOrPropertyWithValue("value", "world")
        .hasFieldOrPropertyWithValue("type", String.class);
    assertThat(parameters.get(3))
        .hasFieldOrPropertyWithValue("value", null)
        .hasFieldOrPropertyWithValue("type", Integer.class);

    // 绑定数组
    spec.bind(
        new Object[] {
          "array1-hello", Parameter.from(100), Parameter.fromOrNull(null, Byte.class), 200L
        });
    assertThat(parameters.get(0))
        .hasFieldOrPropertyWithValue("value", "array1-hello")
        .hasFieldOrPropertyWithValue("type", String.class);
    assertThat(parameters.get(1))
        .hasFieldOrPropertyWithValue("value", 100)
        .hasFieldOrPropertyWithValue("type", Integer.class);
    assertThat(parameters.get(2))
        .hasFieldOrPropertyWithValue("value", null)
        .hasFieldOrPropertyWithValue("type", Byte.class);
    assertThat(parameters.get(3))
        .hasFieldOrPropertyWithValue("value", 200L)
        .hasFieldOrPropertyWithValue("type", Long.class);

    // 绑定 NULL 值
    assertThatNullPointerException().isThrownBy(() -> spec.bind(0, null));
    assertThatNullPointerException().isThrownBy(() -> spec.bind(0, null, null));
    assertThatNullPointerException().isThrownBy(() -> spec.bind(null));
    assertThatNullPointerException().isThrownBy(() -> spec.bind(new Object[] {null}));
  }
}
