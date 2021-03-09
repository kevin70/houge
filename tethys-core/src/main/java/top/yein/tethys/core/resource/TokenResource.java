package top.yein.tethys.core.resource;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.dto.AccessTokenDto;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;

/**
 * 访问令牌 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
@Component
public class TokenResource extends AbstractRestSupport implements RoutingService {

  private final TokenService tokenService;

  /**
   * 默认构造函数.
   *
   * @param tokenService 令牌服务
   */
  public TokenResource(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.post("/token/{uid}", interceptors.auth(this::generateToken));
  }

  /**
   * 生成访问令牌.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  public Mono<Void> generateToken(HttpServerRequest request, HttpServerResponse response) {
    // CORS
    response.header(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");

    var uidStr = request.param("uid");
    if (uidStr == null || uidStr.isEmpty()) {
      throw new BizCodeException(BizCodes.C912);
    }

    return tokenService
        .generateToken(uidStr)
        .flatMap(s -> json(response, new AccessTokenDto().setAccessToken(s)));
  }
}
