package top.yein.tethys.storage;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.r2dbc.spi.Row;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;
import top.yein.tethys.domain.CachedJwtSecret;
import top.yein.tethys.entity.JwtSecret;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * JWT 密钥存储 - PostgreSQL.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class JwtSecretDaoImpl implements JwtSecretDao {

  private static final String INSERT_SQL =
      "INSERT INTO jwt_secrets(id,algorithm,secret_key) VALUES($1,$2,$3)";
  private static final String DELETE_SQL = "UPDATE jwt_secrets SET deleted=$1 WHERE id=$2";
  private static final String FIND_BY_ID_SQL = "SELECT * FROM jwt_secrets WHERE id=$1";
  private static final String FIND_ALL_SQL = "SELECT * FROM jwt_secrets";

  private final R2dbcClient rc;
  private final AsyncCache<String, CachedJwtSecret> jwtSecretCache;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public JwtSecretDaoImpl(R2dbcClient rc) {
    this.rc = rc;
    this.jwtSecretCache =
        Caffeine.newBuilder()
            .recordStats()
            .refreshAfterWrite(REFRESH_CACHE_DURATION)
            .maximumSize(Byte.MAX_VALUE)
            .buildAsync(cacheMappingFunc(Context.empty())::apply);
  }

  @Override
  public Mono<Integer> insert(JwtSecret entity) {
    return rc.sql(INSERT_SQL)
        .bind(new Object[] {entity.getId(), entity.getAlgorithm(), entity.getSecretKey()})
        .rowsUpdated();
  }

  @Override
  public Mono<Integer> delete(String id) {
    return rc.sql(DELETE_SQL)
        .bind(0, System.currentTimeMillis() / 1000)
        .bind(1, id)
        .rowsUpdated()
        .doOnSuccess(unused -> jwtSecretCache.synchronous().invalidate(id));
  }

  @Override
  public Mono<JwtSecret> findById(String id) {
    return rc.sql(FIND_BY_ID_SQL).bind(0, id).map(this::mapEntity).one();
  }

  @Override
  public Flux<JwtSecret> findAll() {
    return rc.sql(FIND_ALL_SQL).map(this::mapEntity).all();
  }

  @Override
  public Flux<CachedJwtSecret> refreshAll() {
    return findAll()
        .map(this::toCachedJwtSecret)
        .doOnNext(
            cachedJwtSecret ->
                jwtSecretCache.put(
                    cachedJwtSecret.getId(), CompletableFuture.completedFuture(cachedJwtSecret)));
  }

  @Override
  public Mono<CachedJwtSecret> loadById(String id) {
    return Mono.deferContextual(
        context -> Mono.fromFuture(jwtSecretCache.get(id, cacheMappingFunc(context))));
  }

  @Override
  public Flux<CachedJwtSecret> loadNoDeleted() {
    var values = jwtSecretCache.asMap().values();
    var publishers = new ArrayList<Mono<CachedJwtSecret>>(values.size());
    for (CompletableFuture<CachedJwtSecret> value : values) {
      publishers.add(Mono.fromFuture(value));
    }
    Predicate<CachedJwtSecret> filterDeleted = cachedJwtSecret -> cachedJwtSecret.getDeleted() == 0;
    return Flux.concat(publishers)
        .filter(filterDeleted)
        .switchIfEmpty(refreshAll().filter(filterDeleted));
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

  private BiFunction<String, Executor, CompletableFuture<CachedJwtSecret>> cacheMappingFunc(
      ContextView context) {
    return (s, executor) -> {
      log.debug("加载 jwt secret [kid={}]", s);
      return findById(s)
          .map(this::toCachedJwtSecret)
          // context 用于事务传递
          .contextWrite(context)
          .toFuture();
    };
  }

  private CachedJwtSecret toCachedJwtSecret(JwtSecret e) {
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
  }
}
