package top.yein.tethys.core.resource;

import com.google.common.net.MediaType;
import io.netty.handler.codec.http.HttpHeaderNames;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.BizCodes;

/**
 * @author KK (kzou227@qq.com)
 * @date 2020-12-29 20:39
 */
public class TokenResource {

  private final AuthService authService;

  /** @param authService */
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
    return Mono.empty();
  }
}
