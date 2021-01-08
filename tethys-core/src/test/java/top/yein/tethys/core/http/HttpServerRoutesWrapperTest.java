package top.yein.tethys.core.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import reactor.test.StepVerifier;

/**
 * {@link HttpServerRoutesWrapper} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class HttpServerRoutesWrapperTest {

  @Test
  void execute() {
    var routes = mock(HttpServerRoutes.class);
    when(routes.directory(any(), any(), any())).thenReturn(routes);
    when(routes.route(any(), any())).thenReturn(routes);
    when(routes.apply(any(), any())).thenReturn(Flux.empty());

    var hsrw = new HttpServerRoutesWrapper(routes);
    assertThat(hsrw.directory(any(), any(), any())).isEqualTo(routes);
    assertThat(hsrw.route(any(), any())).isEqualTo(routes);
    StepVerifier.create(hsrw.apply(any(), any())).verifyComplete();

    verify(routes).directory(any(), any(), any());
    verify(routes).route(any(), any());
    verify(routes).apply(any(), any());
  }

  @Test
  void applyError() {
    var routes = mock(HttpServerRoutes.class);
    var httpExceptionHandler = mock(HttpExceptionHandler.class);
    var request = mock(HttpServerRequest.class);
    var response = mock(HttpServerResponse.class);

    var ex = new IllegalStateException("TEST");
    when(routes.apply(any(), any())).thenThrow(ex);
    when(httpExceptionHandler.apply(any(), any(), any())).thenReturn(Mono.empty());

    var hsrw = new HttpServerRoutesWrapper(routes, httpExceptionHandler);
    var p = hsrw.apply(request, response);
    StepVerifier.create(p).verifyComplete();

    var requestCaptor = ArgumentCaptor.forClass(HttpServerRequest.class);
    var responseCaptor = ArgumentCaptor.forClass(HttpServerResponse.class);
    var exCaptor = ArgumentCaptor.forClass(Exception.class);
    verify(httpExceptionHandler)
        .apply(requestCaptor.capture(), responseCaptor.capture(), exCaptor.capture());

    assertThat(requestCaptor.getValue()).isEqualTo(request);
    assertThat(responseCaptor.getValue()).isEqualTo(response);
    assertThat(exCaptor.getValue()).isEqualTo(ex);
  }
}
