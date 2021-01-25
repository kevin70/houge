package top.yein.tethys.repository;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.r2dbc.spi.Row;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import lombok.extern.log4j.Log4j2;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.domain.CachedJwtSecret;
import top.yein.tethys.entity.JwtSecret;

/**
 * JWT 密钥存储 - PostgreSQL.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class JwtSecretRepositoryImpl implements JwtSecretRepository {

  private static final String INSERT_SQL =
      "INSERT INTO t_jwt_secret(id,algorithm,secret_key) VALUES(:id,:algorithm,:secretKey)";
  private static final String FIND_BY_ID = "SELECT * FROM t_jwt_secret WHERE id=:id";

  private final DatabaseClient dc;
  private final AsyncCache<String, CachedJwtSecret> jwtSecretCache;
  // 缓存映射函数
  private final BiFunction<String, Executor, CompletableFuture<CachedJwtSecret>> cacheMappingFunc =
      (s, executor) -> {
        log.debug("加载 jwt secret [kid={}]", s);
        return findById(s)
            .map(
                e -> {
                  var builder = CachedJwtSecret.builder();
                  var algorithm = SignatureAlgorithm.forName(e.getAlgorithm());
                  var secretKey = Keys.hmacShaKeyFor(e.getSecretKey().array());

                  return builder
                      .id(e.getId())
                      .algorithm(algorithm)
                      .secretKey(secretKey)
                      .deleted(e.getDeleted())
                      .createTime(e.getCreateTime())
                      .updateTime(e.getUpdateTime())
                      .build();
                })
            .toFuture();
      };

  /**
   * 构造函数.
   *
   * @param dc 数据访问客户端
   */
  public JwtSecretRepositoryImpl(DatabaseClient dc) {
    this.dc = dc;
    this.jwtSecretCache =
        Caffeine.newBuilder()
            .recordStats()
            .refreshAfterWrite(REFRESH_CACHE_DURATION)
            .buildAsync(cacheMappingFunc::apply);
  }

  @Override
  public Mono<Integer> insert(JwtSecret entity) {
    return dc.sql(INSERT_SQL)
        .bind("id", entity.getId())
        .bind("algorithm", entity.getAlgorithm())
        .bind("secretKey", entity.getSecretKey())
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Mono<JwtSecret> findById(String id) {
    return dc.sql(FIND_BY_ID).bind("id", id).map(this::mapEntity).first();
  }

  @Override
  public Mono<CachedJwtSecret> loadById(String id) {
    return Mono.defer(() -> Mono.fromFuture(jwtSecretCache.get(id, cacheMappingFunc)));
  }

  @Override
  public Flux<CachedJwtSecret> loadNoDeleted() {
    // FIXME
    return null;
  }

  private JwtSecret mapEntity(Row row) {
    var e = new JwtSecret();
    e.setId(row.get("id", String.class));
    e.setAlgorithm(row.get("algorithm", String.class));
    e.setSecretKey(row.get("secret_key", ByteBuffer.class));
    e.setDeleted(row.get("deleted", Integer.class));
    e.setCreateTime(row.get("create_time", LocalDateTime.class));
    e.setUpdateTime(row.get("update_time", LocalDateTime.class));
    return e;
  }
}
