package top.yein.tethys.core.resource;

import java.util.function.BiFunction;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.http.AbstractRestSupport;

/**
 * 用户认证拦截器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class AuthenticationInterceptor extends AbstractRestSupport {

  private static final String ACCESS_TOKEN_QUERY_NAME = "access_token";

  private final AuthService authService;

  /**
   * 构造函数.
   *
   * @param authService 认证服务
   */
  @Inject
  public AuthenticationInterceptor(AuthService authService) {
    this.authService = authService;
  }

  /**
   * 请求认证.
   *
   * @param next 认证成功后执行的处理函数
   * @return RS
   */
  public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> handle(
      BiFunction<? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>>
          next) {
    return (request, response) -> {
      var accessToken = queryParam(request, ACCESS_TOKEN_QUERY_NAME);
      if (accessToken == null || accessToken.isEmpty()) {
        throw new BizCodeException(BizCode.C401, "缺少 access_token");
      }
      return authService
          .authenticate(accessToken)
          .flatMap((unused) -> Mono.from(next.apply(request, response)));
    };
  }
}
