package top.yein.tethys.core.resource;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;
import reactor.test.StepVerifier;
import top.yein.chaos.biz.BizCode;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.TestUtils;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.session.TestAuthContext;
import top.yein.tethys.core.test.MockHttpServerRequest;

/**
 * {@link AuthInterceptor} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class AuthInterceptorTest {

  @Test
  void handle() {
    var authService = mock(AuthService.class);
    var authContext = new TestAuthContext();
    when(authService.authenticate(anyString())).thenReturn(Mono.just(authContext));

    var interceptor = new AuthInterceptor(authService);
    var fun = interceptor.handle((request, response) -> Mono.empty());

    var request =
        MockHttpServerRequest.builder()
            .uri("/test?access_token=HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH")
            .build();
    var response = mock(HttpServerResponse.class);
    var p = fun.apply(request, response);
    StepVerifier.create(p)
        .expectAccessibleContext()
        .contains(AbstractRestSupport.AUTH_CONTEXT_KEY, authContext)
        .then()
        .expectComplete()
        .verify();
  }

  @DisplayName("access_token 为 null")
  @Test
  void handle2() {
    var authService = mock(AuthService.class);
    when(authService.authenticate(anyString())).thenReturn(Mono.just(new TestAuthContext()));

    var interceptor = new AuthInterceptor(authService);
    var fun = interceptor.handle((request, response) -> Mono.empty());

    var request = MockHttpServerRequest.builder().uri("/test").build();
    var response = mock(HttpServerResponse.class);
    var p = fun.apply(request, response);
    StepVerifier.create(p).expectErrorMatches((e) -> TestUtils.test(e, BizCode.C401)).verify();
  }

  @DisplayName("access_token 为 empty")
  @Test
  void handle3() {
    var authService = mock(AuthService.class);
    when(authService.authenticate(anyString())).thenReturn(Mono.just(new TestAuthContext()));

    var interceptor = new AuthInterceptor(authService);
    var fun = interceptor.handle((request, response) -> Mono.empty());

    var request = MockHttpServerRequest.builder().uri("/test?access_token").build();
    var response = mock(HttpServerResponse.class);
    var p = fun.apply(request, response);
    StepVerifier.create(p).expectErrorMatches((e) -> TestUtils.test(e, BizCode.C401)).verify();
  }
}
