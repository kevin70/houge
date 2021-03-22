package top.yein.tethys.core.auth;

import io.jsonwebtoken.Jwts;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.Nil;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.entity.User;
import top.yein.tethys.storage.JwtSecretDao;
import top.yein.tethys.storage.UserDao;
import top.yein.tethys.storage.query.UserQueryDao;

/**
 * 访问令牌实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class TokenServiceImpl implements TokenService {

  private final TokenProps tokenProps;
  private final JwtSecretDao jwtSecretDao;
  private final UserDao userDao;
  private final UserQueryDao userQueryDao;

  /**
   * 使用 JWT 密钥数据访问对象构建对象.
   *
   * @param tokenProps 令牌配置.
   * @param jwtSecretDao JWT 密钥数据访问对象
   * @param userDao 用户访问数据对象
   * @param userQueryDao 用户查询数据访问对象
   */
  @Inject
  public TokenServiceImpl(
      TokenProps tokenProps,
      JwtSecretDao jwtSecretDao,
      UserDao userDao,
      UserQueryDao userQueryDao) {
    this.tokenProps = tokenProps;
    this.jwtSecretDao = jwtSecretDao;
    this.userDao = userDao;
    this.userQueryDao = userQueryDao;
  }

  @Override
  public Mono<String> generateToken(long uid) {
    if (!tokenProps.getGenerator().isTestEnabled()) {
      return Mono.error(new BizCodeException(BizCodes.C403, "当前运行环境禁止访问测试令牌生成接口"));
    }

    return userQueryDao
        .existsById(uid)
        .flatMap(
            b -> {
              // 如果用户不存在则向数据库保存用户信息
              if (!b) {
                // TIPS: 这里是否需要为自动保存用户信息单独设置开关
                // TIPS: 这里是否需要增加额外的配置开关, 确认是否开启自动保存用户信息
                var entity = new User();
                entity.setId(uid);
                entity.setOriginUid(String.valueOf(uid));
                return userDao.insert(entity).then(Nil.mono());
              }
              return Nil.mono();
            })
        .flatMap((unused) -> this.generateToken0(uid));
  }

  private Mono<String> generateToken0(long uid) {
    return jwtSecretDao
        .loadNoDeleted()
        .switchIfEmpty(Flux.error(() -> new BizCodeException(BizCodes.C3310)))
        .next()
        .map(
            cachedJwtSecret -> {
              Map<String, Object> header = Jwts.jwsHeader().setKeyId(cachedJwtSecret.getId());
              var claims = Jwts.claims().setId(String.valueOf(uid));
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
