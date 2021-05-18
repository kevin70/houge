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
package top.yein.tethys.rest.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.net.MediaType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.ReadOnlyHttpHeaders;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.test.StepVerifier;
import reactor.util.context.Context;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.AuthContext;
import top.yein.tethys.BizCodes;
import top.yein.tethys.util.JsonUtils;

/** @author KK (kzou227@qq.com) */
class AbstractRestSupportTest {

  @Data
  static class TestBodyVo {
    String firstName;
    String lastName;
  }

  AbstractRestSupport resource = new AbstractRestSupport() {};

  @Test
  void combineQueryParam() {
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
    when(request.uri())
        .thenReturn(
            "/test?q1=K&q2=L&q_int=5&q_long=54&q_int_invalid=a5&q_long_invalid=a7&q_datetime=2011-12-03T10:15:30");
    assertThat(resource.queryParam(request, "q1", "55")).isEqualTo("K");
    assertThat(resource.queryParam(request, "q_string_no", "55")).isEqualTo("55");
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> resource.requiredQueryParam(request, "q_string_required"))
        .matches(e -> e.getBizCode() == BizCodes.C912);

    assertThat(resource.queryInt(request, "q_int", 55)).isEqualTo(5);
    assertThat(resource.queryInt(request, "q_int_no", 55)).isEqualTo(55);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> resource.requiredQueryInt(request, "q_int_no"))
        .matches(e -> e.getBizCode() == BizCodes.C912);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> resource.queryInt(request, "q_int_invalid", 55))
        .matches(e -> e.getBizCode() == BizCodes.C910);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> resource.requiredQueryInt(request, "q_int_invalid"))
        .matches(e -> e.getBizCode() == BizCodes.C910);

    assertThat(resource.queryLong(request, "q_long", 55)).isEqualTo(54);
    assertThat(resource.queryLong(request, "q_long_no", 55)).isEqualTo(55);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> resource.requiredQueryLong(request, "q_long_no"))
        .matches(e -> e.getBizCode() == BizCodes.C912);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> resource.queryLong(request, "q_long_invalid", 55))
        .matches(e -> e.getBizCode() == BizCodes.C910);
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> resource.requiredQueryLong(request, "q_long_invalid"))
        .matches(e -> e.getBizCode() == BizCodes.C910);

    var now = LocalDateTime.now();
    assertThat(resource.queryDateTime(request, "q_datetime_no", () -> now)).isEqualTo(now);
    assertThat(resource.queryDateTime(request, "q_datetime", () -> now))
        .isEqualTo("2011-12-03T10:15:30");
    assertThatExceptionOfType(BizCodeException.class)
        .isThrownBy(() -> resource.queryDateTime(request, "q1", () -> now))
        .matches(e -> e.getBizCode() == BizCodes.C910);
  }

  @Test
  void queryParam() {
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
    var q1 = resource.queryParam(request, "q1");
    assertThat(q1).isEqualTo("K");

    var q2 = resource.queryParam(request, "q2");
    assertThat(q2).isEqualTo("L");

    assertThat(resource.queryParam(request, "qx")).isNull();
  }

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
    var q1 = resource.queryParams(request, "q1");
    assertThat(q1).contains("K");

    var q2 = resource.queryParams(request, "q2");
    assertThat(q2).contains("L");

    assertThat(resource.queryParams(request, "qx")).isEmpty();
  }

  @Test
  void testQueryParams() {
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
    var params = resource.queryParams(request);
    assertThat(params).containsKeys("q1", "q2");
  }

  @Test
  void requestJson() {
    var byteBuf = ByteBufAllocator.DEFAULT.buffer();
    byteBuf.writeCharSequence("{\"first_name\":\"K\",\"last_name\":\"Z\"}", StandardCharsets.UTF_8);
    var httpContent = new DefaultHttpContent(byteBuf);

    var request = mock(HttpServerRequest.class);
    when(request.receiveContent()).thenReturn(Flux.just(httpContent));
    when(request.requestHeaders())
        .thenReturn(
            new ReadOnlyHttpHeaders(
                true, HttpHeaderNames.CONTENT_TYPE, MediaType.JSON_UTF_8.toString()));

    var p = resource.json(request, TestBodyVo.class);
    StepVerifier.create(p)
        .assertNext(
            vo ->
                assertThat(vo)
                    .hasFieldOrPropertyWithValue("firstName", "K")
                    .hasFieldOrPropertyWithValue("lastName", "Z"))
        .verifyComplete();
  }

  @Test
  void responseJson() throws IOException {
    var response = mock(HttpServerResponse.class);
    var outbound = mock(NettyOutbound.class);
    when(response.alloc()).thenReturn(ByteBufAllocator.DEFAULT);
    when(response.header(any(), any())).thenReturn(response);
    when(response.send(any())).thenReturn(outbound);
    when(outbound.then()).thenReturn(Mono.empty());

    var value = new TestBodyVo();
    value.setFirstName("K");
    value.setLastName("Z");
    var p = resource.json(response, value);
    // 校验 publisher
    StepVerifier.create(p).verifyComplete();

    // 校验 HTTP header
    ArgumentCaptor<CharSequence> headerNameCaptor = ArgumentCaptor.forClass(CharSequence.class);
    ArgumentCaptor<CharSequence> headerValueCaptor = ArgumentCaptor.forClass(CharSequence.class);
    verify(response).header(headerNameCaptor.capture(), headerValueCaptor.capture());
    assertThat(headerNameCaptor.getValue()).isEqualToIgnoringCase(HttpHeaderNames.CONTENT_TYPE);
    assertThat(MediaType.JSON_UTF_8.is(MediaType.parse(headerValueCaptor.getValue().toString())))
        .isTrue();

    // 校验 HTTP body
    ArgumentCaptor<Mono<ByteBuf>> bodyMonoCaptor = ArgumentCaptor.forClass(Mono.class);
    verify(response).send(bodyMonoCaptor.capture());
    InputStream in = new ByteBufInputStream(bodyMonoCaptor.getValue().block());
    var vo = JsonUtils.objectMapper().readValue(in, TestBodyVo.class);
    assertThat(vo)
        .hasFieldOrPropertyWithValue("firstName", value.getFirstName())
        .hasFieldOrPropertyWithValue("lastName", value.getLastName());
  }

  @Test
  void authContext() {
    StepVerifier.create(resource.authContext())
        .expectErrorMatches(
            e -> {
              var ex = (BizCodeException) e;
              return ex.getBizCode() == BizCode.C401;
            })
        .verify();

    var ac = mock(AuthContext.class);
    StepVerifier.create(
            Mono.defer(() -> resource.authContext())
                .contextWrite(Context.of(AbstractRestSupport.AUTH_CONTEXT_KEY, ac)))
        .expectNext(ac)
        .expectComplete()
        .verify();
  }
}
