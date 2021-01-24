package top.yein.tethys.core.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.tethys.auth.TokenService;

/**
 * 访问令牌实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class TokenServiceImpl implements TokenService {

  private final Map<String, SecretKey> jwtSecrets;

  public TokenServiceImpl(Map<String, SecretKey> jwtSecrets) {
    this.jwtSecrets = jwtSecrets;
  }

  @Override
  public Mono<String> generateToken(long uid) {
    var keys = new ArrayList<>(jwtSecrets.entrySet());
    Collections.shuffle(keys);

    var entry = keys.get(0);
    Map<String, Object> header = Jwts.jwsHeader().setKeyId(entry.getKey());
    var claims = Jwts.claims().setId(String.valueOf(uid));
    var token =
        Jwts.builder()
            .signWith(entry.getValue(), SignatureAlgorithm.HS512)
            .setHeader(header)
            .setClaims(claims)
            .compact();

    log.info("生成访问令牌 [uid={}, access_token={}]", uid, token);
    return Mono.just(token);
  }
}
