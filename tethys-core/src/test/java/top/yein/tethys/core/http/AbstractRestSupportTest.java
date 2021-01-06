package top.yein.tethys.core.http;

import static org.assertj.core.api.Assertions.assertThat;
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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
import top.yein.tethys.util.JsonUtils;

/**
 * @author KK (kzou227@qq.com)
 */
class AbstractRestSupportTest {

  @Data
  static class TestBodyVo {
    String firstName;
    String lastName;
  }

  AbstractRestSupport resource = new AbstractRestSupport() {};

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

    assertThat(resource.queryParams(request, "qx")).isNull();
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
}
