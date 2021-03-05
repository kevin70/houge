package top.yein.tethys.core.resource;

import io.netty.handler.codec.http.HttpHeaderNames;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.dto.AccessTokenDto;
import top.yein.tethys.core.http.AbstractRestSupport;

/**
 * 访问令牌 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class TokenResource extends AbstractRestSupport {

  private final TokenService tokenService;

  /**
   * 默认构造函数.
   *
   * @param tokenService 令牌服务
   */
  public TokenResource(TokenService tokenService) {
    this.tokenService = tokenService;
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

    long uid;
    try {
      uid = Long.parseLong(uidStr);
    } catch (NumberFormatException e) {
      throw new BizCodeException(BizCodes.C911, "非法的 uid: " + uidStr)
          .addContextValue("uid", uidStr);
    }
    return tokenService
        .generateToken(uid)
        .flatMap(s -> json(response, new AccessTokenDto().setAccessToken(s)));
  }
}
