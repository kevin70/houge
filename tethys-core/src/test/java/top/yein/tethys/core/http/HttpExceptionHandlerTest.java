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
package top.yein.tethys.core.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.test.StepVerifier;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.domain.Problem;

/**
 * {@link HttpExceptionHandler} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class HttpExceptionHandlerTest {

  @Test
  void applyEnabledDebug() {
    var request = mock(HttpServerRequest.class);
    var response = mock(HttpServerResponse.class);
    when(request.uri()).thenReturn("/test?debug");
    when(response.status(any())).thenReturn(response);

    // SPY
    var httpExceptionHandler = spy(new HttpExceptionHandler());
    doReturn("").when(httpExceptionHandler).queryParam(eq(request), eq("debug"));
    doReturn(Mono.empty()).when(httpExceptionHandler).json(eq(response), any());

    var p = httpExceptionHandler.apply(request, response, new IllegalStateException());
    StepVerifier.create(p).verifyComplete();

    // 捕捉参数并校验
    var statusCaptor = ArgumentCaptor.forClass(Integer.class);
    var problemCaptor = ArgumentCaptor.forClass(Problem.class);
    verify(response).status(statusCaptor.capture());
    verify(httpExceptionHandler).json(any(), problemCaptor.capture());

    assertThat(statusCaptor.getValue()).isEqualTo(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());

    var problem = problemCaptor.getValue();
    assertThat(problem)
        .hasFieldOrPropertyWithValue("status", HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    // 开启调试模式下将 stacktrace 通过接口输出
    assertThat(problem.getProperties()).containsKey("stacktrace");
  }

  @Test
  void applyNoDebug() {
    var request = mock(HttpServerRequest.class);
    var response = mock(HttpServerResponse.class);
    when(request.uri()).thenReturn("/test");
    when(response.status(any())).thenReturn(response);

    // SPY
    var httpExceptionHandler = spy(new HttpExceptionHandler(false));
    doReturn(null).when(httpExceptionHandler).queryParam(eq(request), eq("debug"));
    doReturn(Mono.empty()).when(httpExceptionHandler).json(eq(response), any());

    var p = httpExceptionHandler.apply(request, response, new IllegalStateException());
    StepVerifier.create(p).verifyComplete();

    // 捕捉参数并校验
    var statusCaptor = ArgumentCaptor.forClass(Integer.class);
    var problemCaptor = ArgumentCaptor.forClass(Problem.class);
    verify(response).status(statusCaptor.capture());
    verify(httpExceptionHandler).json(any(), problemCaptor.capture());

    assertThat(statusCaptor.getValue()).isEqualTo(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());

    var problem = problemCaptor.getValue();
    assertThat(problem)
        .hasFieldOrPropertyWithValue("status", HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    // 开启调试模式下将 stacktrace 通过接口输出
    assertThat(problem.getProperties()).isNullOrEmpty();
  }

  @Test
  void applyBizCodeException() {
    var request = mock(HttpServerRequest.class);
    var response = mock(HttpServerResponse.class);
    when(request.uri()).thenReturn("/test");
    when(response.status(any())).thenReturn(response);

    // SPY
    var httpExceptionHandler = spy(new HttpExceptionHandler());
    doReturn("").when(httpExceptionHandler).queryParam(eq(request), eq("debug"));
    doReturn(Mono.empty()).when(httpExceptionHandler).json(eq(response), any());

    var ex = new BizCodeException(BizCode.C401).addContextValue("hello", "test");
    var p = httpExceptionHandler.apply(request, response, ex);
    StepVerifier.create(p).verifyComplete();

    // 捕捉参数并校验
    var statusCaptor = ArgumentCaptor.forClass(Integer.class);
    var problemCaptor = ArgumentCaptor.forClass(Problem.class);
    verify(response).status(statusCaptor.capture());
    verify(httpExceptionHandler).json(any(), problemCaptor.capture());

    assertThat(statusCaptor.getValue()).isEqualTo(ex.getBizCode().getStatus());

    var problem = problemCaptor.getValue();
    assertThat(problem).hasFieldOrPropertyWithValue("status", ex.getBizCode().getStatus());
    // 开启调试模式下将 stacktrace 通过接口输出
    assertThat(problem.getProperties()).containsKey("context_values");
  }
}
