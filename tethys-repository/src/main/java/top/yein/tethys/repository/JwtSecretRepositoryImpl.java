package top.yein.tethys.repository;

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
import lombok.extern.log4j.Log4j2;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;
import top.yein.tethys.domain.CachedJwtSecret;
import top.yein.tethys.entity.JwtSecret;

/**
 * JWT 密钥存储 - PostgreSQL.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
@Repository
public class JwtSecretRepositoryImpl implements JwtSecretRepository {

  private static final String INSERT_SQL =
      "INSERT INTO t_jwt_secret(id,algorithm,secret_key) VALUES(:id,:algorithm,:secretKey)";
  private static final String DELETE_SQL = "UPDATE t_jwt_secret SET deleted=:deleted WHERE id=:id";
  private static final String FIND_BY_ID_SQL = "SELECT * FROM t_jwt_secret WHERE id=:id";
  private static final String FIND_ALL_SQL = "SELECT * FROM t_jwt_secret";

  private final DatabaseClient dc;
  private final AsyncCache<String, CachedJwtSecret> jwtSecretCache;

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
            .maximumSize(Byte.MAX_VALUE)
            .buildAsync(cacheMappingFunc(Context.empty())::apply);
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
  public Mono<Integer> delete(String id) {
    return dc.sql(DELETE_SQL)
        .bind("deleted", System.currentTimeMillis() / 1000)
        .bind("id", id)
        .fetch()
        .rowsUpdated()
        .doOnSuccess(unused -> jwtSecretCache.synchronous().invalidate(id));
  }

  @Override
  public Mono<JwtSecret> findById(String id) {
    return dc.sql(FIND_BY_ID_SQL).bind("id", id).map(this::mapEntity).first();
  }

  @Override
  public Flux<JwtSecret> findAll() {
    return dc.sql(FIND_ALL_SQL).map(this::mapEntity).all();
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
          // context 用于 spring 事务传递
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
