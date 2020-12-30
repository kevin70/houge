package top.yein.tethys.core.resource;

import com.google.common.net.MediaType;
import io.netty.handler.codec.http.HttpHeaderNames;
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
 * @author KK (kzou227@qq.com)
 * @date 2020-12-29 20:39
 */
public class TokenResource extends AbstractRestSupport {

  private final AuthService authService;

  /**
   * 构建.
   *
   * @param authService
   */
  @Inject
  public TokenResource(AuthService authService) {
    this.authService = authService;
  }

  /**
   * @param request
   * @param response
   * @return
   */
  public Mono<Void> generateToken(HttpServerRequest request, HttpServerResponse response) {
    response.header(HttpHeaderNames.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
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
