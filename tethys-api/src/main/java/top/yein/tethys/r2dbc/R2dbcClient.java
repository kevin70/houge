package top.yein.tethys.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.Map;
import java.util.function.BiFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <a href="https://r2dbc.io/">R2DBC</a> 响应式客户端接口规范定义.
 *
 * @author KK (kzou227@qq.com)
 */
public interface R2dbcClient {

  /**
   * 返回 R2DBC 连接工厂对象.
   *
   * @return R2DBC 连接工厂对象.
   */
  ConnectionFactory getConnectionFactory();

  /**
   * 返回执行规范并绑定指定的 SQL.
   *
   * @param sql 需执行的 SQL
   * @return 执行的规范对象
   */
  ExecuteSpec sql(String sql);

  /** 用于指定 SQL 的执行规范. */
  interface ExecuteSpec {

    /**
     * 使用索引绑定 SQL 参数值.
     *
     * @param index 索引
     * @param value 参数值且不能为 NULL
     * @return self
     */
    ExecuteSpec bind(int index, Object value);

    /**
     * 使用索引绑定 SQL 参数值.
     *
     * <p>如果参数值为 NULL 则绑定数据类型的默认 NULL 值.
     *
     * @param index 索引
     * @param value 参数值
     * @param type 参数类型
     * @return self
     */
    ExecuteSpec bind(int index, Object value, Class<?> type);

    /**
     * 按照数组的索引绑定 SQL 参数值.
     *
     * <p>{@code parameters} 不能为 NULL 同时其元素也不能为 NULL.
     *
     * @param parameters 参数值数组
     * @return self
     */
    ExecuteSpec bind(Object[] parameters);

    /**
     * 返回获取数据规范并绑定数据结果映射函数.
     *
     * @param mappingFunction 数据映射函数
     * @param <R> 映射的数据类型
     * @return 获取数据规范
     */
    <R> FetchSpec<R> map(BiFunction<Row, RowMetadata, R> mappingFunction);

    /**
     * 返回获取数据规范并将数据映射为 {@code Map} 类型.
     *
     * @return 获取数据规范
     */
    FetchSpec<Map<String, Object>> fetch();

    /**
     * 返回更新数据库受影响的行数.
     *
     * @return 受影响的行数
     */
    Mono<Integer> rowsUpdated();
  }

  /**
   * 获取数据规范.
   *
   * @param <T> 映射的数据类型
   */
  interface FetchSpec<T> {

    /**
     * 只返回一条数据.
     *
     * <p>如果没有数据则返回 {@code Mono.empty()}, 如果数据行数超过 1 则会抛出异常 {@link IncorrectResultSizeException} 异常.
     *
     * @return 数据
     * @see IncorrectResultSizeException
     */
    Mono<T> one();

    /**
     * 返回所有的查询数据.
     *
     * @return 数据
     */
    Flux<T> all();
  }
}
