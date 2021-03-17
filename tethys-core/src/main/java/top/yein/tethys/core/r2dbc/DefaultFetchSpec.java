package top.yein.tethys.core.r2dbc;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.r2dbc.IncorrectResultSizeException;
import top.yein.tethys.r2dbc.R2dbcClient.FetchSpec;

/**
 * 默认实现.
 *
 * @author KK (kzou227@qq.com)
 */
class DefaultFetchSpec<R> implements FetchSpec<R> {

  final String sql;
  final Publisher<? extends Connection> connectionSource;
  final Function<Connection, Statement> statementFunction;
  final Function<Result, Publisher<R>> resultMappingFunction;

  DefaultFetchSpec(
      String sql,
      Publisher<? extends Connection> connectionSource,
      Function<Connection, Statement> statementFunction,
      Function<Result, Publisher<R>> resultMappingFunction) {
    this.sql = sql;
    this.connectionSource = connectionSource;
    this.statementFunction = statementFunction;
    this.resultMappingFunction = resultMappingFunction;
  }

  @Override
  public Mono<R> one() {
    return all()
        .buffer(2)
        .flatMap(
            list -> {
              if (list.isEmpty()) {
                return Mono.empty();
              }
              if (list.size() > 1) {
                return Mono.error(
                    new IncorrectResultSizeException(String.format("查询[%s]返回的结果数量与预期不符", sql), 1));
              }
              return Mono.just(list.get(0));
            })
        .next();
  }

  @Override
  public Flux<R> all() {
    return Flux.from(connectionSource)
        .flatMap(
            connection ->
                Flux.from(statementFunction.apply(connection).execute())
                    .flatMap(resultMappingFunction));
  }
}
