package top.yein.tethys.core.resource;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.dto.AccessTokenDto;
import top.yein.tethys.core.http.AbstractRestSupport;

/**
 * 访问令牌 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
public class TokenResource extends AbstractRestSupport {

  private final AuthService authService;

  /**
   * 默认构造函数.
   *
   * @param authService 认证服务
   */
  @Inject
  public TokenResource(AuthService authService) {
    this.authService = authService;
  }

  /**
   * 生成访问令牌.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  public Mono<Void> generateToken(HttpServerRequest request, HttpServerResponse response) {
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
    return authService
        .generateToken(uid)
        .flatMap(s -> json(response, new AccessTokenDto().setAccessToken(s)));
  }
}
