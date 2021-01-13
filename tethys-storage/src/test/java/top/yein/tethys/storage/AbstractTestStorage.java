package top.yein.tethys.storage;

import com.typesafe.config.ConfigFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import top.yein.tethys.ConfigKeys;

/**
 * 测试存储的基类.
 *
 * @author KK (kzou227@qq.com)
 */
public abstract class AbstractTestStorage {

  /** 数据库链接. */
  protected final ConnectionFactory connectionFactory;

  protected AbstractTestStorage() {
    var config = ConfigFactory.parseResources("tethys.conf");
    var r2dbcUrl = config.getString(ConfigKeys.MESSAGE_STORAGE_R2DBC_URL);

    // 初始化数据库链接
    this.connectionFactory = ConnectionFactories.get(r2dbcUrl);
  }

  /**
   * @param mono
   * @param <T>
   * @return
   */
  protected <T> Mono<T> transactional(Mono<T> mono) {
    return Mono.defer(() -> mono).contextWrite(newContext());
  }

  /**
   * @param flux
   * @param <T>
   * @return
   */
  protected <T> Flux<T> transactional(Flux<T> flux) {
    return Flux.defer(() -> flux).contextWrite(newContext());
  }

  /**
   * @param sql
   * @param params
   * @return
   */
  protected Map<String, Object> findOne(String sql, Map<String, Object> params) {
    return execute(sql, params)
        .flatMap(
            result ->
                result.map(
                    (row, rowMetadata) -> {
                      Map<String, Object> map = new LinkedHashMap<>();
                      for (String columnName : rowMetadata.getColumnNames()) {
                        map.put(columnName, row.get(columnName));
                      }
                      return map;
                    }))
        .blockFirst();
  }

  /**
   * @param sql
   * @param params
   * @return
   */
  protected List<Map<String, Object>> findList(String sql, Map<String, Object> params) {
    var listBuilder = ImmutableList.<Map<String, Object>>builder();
    execute(sql, params)
        .flatMap(
            result ->
                result.map(
                    (row, rowMetadata) -> {
                      Map<String, Object> map = new LinkedHashMap<>();
                      for (String columnName : rowMetadata.getColumnNames()) {
                        map.put(columnName, row.get(columnName));
                      }
                      listBuilder.add(map);
                      return map;
                    }))
        .blockLast();
    return listBuilder.build();
  }

  /**
   * @param sql
   * @param params
   */
  protected void executeSql(String sql, Map<String, Object> params) {
    execute(sql, params).blockLast();
  }

  private Flux<Result> execute(String sql, Map<String, Object> params) {
    return Flux.from(connectionFactory.create())
        .flatMap(
            connection -> {
              var statement = connection.createStatement(sql);
              for (Entry<String, Object> entry : params.entrySet()) {
                statement.bind(entry.getKey(), entry.getValue());
              }
              return statement.execute();
            });
  }

  private final Context newContext() {
    return Context.of(ConnectionFactory.class, connectionFactory);
  }
}
