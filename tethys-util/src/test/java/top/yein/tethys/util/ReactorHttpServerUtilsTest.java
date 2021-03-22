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
package top.yein.tethys.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.netty.channel.embedded.EmbeddedChannel;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;

/**
 * {@link ReactorHttpServerUtils} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class ReactorHttpServerUtilsTest {

  @Test
  void queryParams() {
    var channel = new EmbeddedChannel();
    var connection = mock(Connection.class);
    when(connection.channel()).thenReturn(channel);

    var request = mock(HttpServerRequest.class);
    when(request.withConnection(any()))
        .then(
            invocation -> {
              Consumer<Connection> consumer = invocation.getArgument(0);
              consumer.accept(connection);
              return request;
            });
    when(request.uri()).thenReturn("/test?q1=K&q2=L");
    var q1 = ReactorHttpServerUtils.queryParam(request, "q1");
    assertThat(q1).isEqualTo("K");

    var q2 = ReactorHttpServerUtils.queryParam(request, "q2");
    assertThat(q2).isEqualTo("L");

    assertThat(ReactorHttpServerUtils.queryParam(request, "qx")).isNull();
  }

  @Test
  void queryParams_list() {
    var channel = new EmbeddedChannel();
    var connection = mock(Connection.class);
    when(connection.channel()).thenReturn(channel);

    var request = mock(HttpServerRequest.class);
    when(request.withConnection(any()))
        .then(
            invocation -> {
              Consumer<Connection> consumer = invocation.getArgument(0);
              consumer.accept(connection);
              return request;
            });
    when(request.uri()).thenReturn("/test?q1=K&q2=L");
    var q1 = ReactorHttpServerUtils.queryParams(request, "q1");
    assertThat(q1).contains("K");

    var q2 = ReactorHttpServerUtils.queryParams(request, "q2");
    assertThat(q2).contains("L");

    assertThat(ReactorHttpServerUtils.queryParams(request, "qx")).isNull();
  }

  @Test
  void queryParams_map() {
    var channel = new EmbeddedChannel();
    var connection = mock(Connection.class);
    when(connection.channel()).thenReturn(channel);

    var request = mock(HttpServerRequest.class);
    when(request.withConnection(any()))
        .then(
            invocation -> {
              Consumer<Connection> consumer = invocation.getArgument(0);
              consumer.accept(connection);
              return request;
            });
    when(request.uri()).thenReturn("/test?q1=K&q2=L");
    var params = ReactorHttpServerUtils.queryParams(request);
    assertThat(params).containsKeys("q1", "q2");

    var attr1 = channel.attr(ReactorHttpServerUtils.QUERY_PARAMS_ATTRIBUTE_KEY);
    var attr2 = channel.attr(ReactorHttpServerUtils.QUERY_PARAMS_ATTRIBUTE_KEY);
    assertThat(attr1).isEqualTo(attr2);
  }

  @Test
  void getConnection() {
    var connection = mock(Connection.class);
    var request = mock(HttpServerRequest.class);
    when(request.withConnection(any()))
        .then(
            invocation -> {
              Consumer<Connection> consumer = invocation.getArgument(0);
              consumer.accept(connection);
              return request;
            });

    var actual = ReactorHttpServerUtils.getConnection(request);
    verify(request).withConnection(any());
    assertThat(actual).isEqualTo(connection);
  }
}
