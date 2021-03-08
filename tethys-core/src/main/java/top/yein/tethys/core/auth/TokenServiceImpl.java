package top.yein.tethys.core.auth;

import io.jsonwebtoken.Jwts;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.repository.JwtSecretRepository;

/**
 * 访问令牌实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class TokenServiceImpl implements TokenService {

  private final JwtSecretRepository jwtSecretRepository;

  /** @param jwtSecretRepository */
  public TokenServiceImpl(JwtSecretRepository jwtSecretRepository) {
    this.jwtSecretRepository = jwtSecretRepository;
  }

  @Override
  public Mono<String> generateToken(String uid) {
    return jwtSecretRepository
        .loadNoDeleted()
        .switchIfEmpty(Flux.error(() -> new BizCodeException(BizCodes.C3310)))
        .next()
        .map(
            cachedJwtSecret -> {
              Map<String, Object> header = Jwts.jwsHeader().setKeyId(cachedJwtSecret.getId());
              var claims = Jwts.claims().setId(uid);
              var token =
                  Jwts.builder()
                      .signWith(cachedJwtSecret.getSecretKey(), cachedJwtSecret.getAlgorithm())
                      .setHeader(header)
                      .setClaims(claims)
                      .compact();

              log.info(
                  "生成访问令牌 [kid={}, uid={}, access_token={}]", cachedJwtSecret.getId(), uid, token);
              return token;
            });
  }
}
