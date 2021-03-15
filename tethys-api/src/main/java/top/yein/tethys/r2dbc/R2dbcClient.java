package top.yein.tethys.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.Map;
import java.util.function.BiFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** @author KK (kzou227@qq.com) */
public interface R2dbcClient {

  /** @return */
  ConnectionFactory getConnectionFactory();

  /**
   * @param sql
   * @return
   */
  ExecuteSpec sql(String sql);

  /** */
  interface ExecuteSpec {

    /**
     * @param index
     * @param value
     * @return
     */
    ExecuteSpec bind(int index, Object value);

    /**
     * @param index
     * @param value
     * @param type
     * @return
     */
    ExecuteSpec bind(int index, Object value, Class<?> type);

    /**
     * @param parameters
     * @return
     */
    ExecuteSpec bind(Object[] parameters);

    /**
     * @param mappingFunction
     * @param <R>
     * @return
     */
    <R> FetchSpec<R> map(BiFunction<Row, RowMetadata, R> mappingFunction);

    /** @return */
    FetchSpec<Map<String, Object>> fetch();

    /** @return */
    Mono<Integer> rowsUpdated();

    /** @return */
    Mono<Void> then();
  }

  /** @param <T> */
  interface FetchSpec<T> {

    /** @return */
    Mono<T> one();

    /** @return */
    Mono<T> first();

    /** @return */
    Flux<T> all();
  }
}
