package io.zhudy.xim.auth.impl;

import static io.zhudy.xim.BizCodes.C3300;
import static io.zhudy.xim.BizCodes.C3301;
import static io.zhudy.xim.BizCodes.C3302;
import static io.zhudy.xim.BizCodes.C3305;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.SigningKeyResolver;
import io.zhudy.xim.BizCodeException;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.auth.AuthService;
import javax.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * <a href="https://tools.ietf.org/html/rfc7515">JWS</a> 用户认证服务实现.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class JwsAuthService implements AuthService {

  private final JwtParser jwtParser;

  @Inject
  public JwsAuthService(SigningKeyResolver keyResolver) {
    jwtParser = Jwts.parserBuilder().setSigningKeyResolver(keyResolver).build();
  }

  @Override
  public Mono<AuthContext> authorize(String token) {
    return Mono.create(
        sink -> {
          try {
            var jws = jwtParser.parseClaimsJws(token);
            var authContext = new JwsAuthContext(token, jws.getBody());
            sink.success(authContext);
          } catch (MalformedJwtException e) {
            sink.error(new BizCodeException(C3300, e.getMessage()));
          } catch (ExpiredJwtException e) {
            sink.error(new BizCodeException(C3301, e.getMessage()));
          } catch (PrematureJwtException e) {
            sink.error(new BizCodeException(C3302, e.getMessage()));
          } catch (Exception e) {
            sink.error(new BizCodeException(C3305, e.getMessage()));
          }
        });
  }
}
