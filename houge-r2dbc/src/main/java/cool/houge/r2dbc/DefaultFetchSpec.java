/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cool.houge.r2dbc;

import cool.houge.r2dbc.R2dbcClient.FetchSpec;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 默认实现.
 *
 * @author KK (kzou227@qq.com)
 */
class DefaultFetchSpec<R> implements FetchSpec<R> {

  final String sql;
  final ConnectionAccessor connectionAccessor;
  final Function<Connection, Statement> statementFunction;
  final Function<Result, Publisher<R>> resultMappingFunction;

  DefaultFetchSpec(
      String sql,
      ConnectionAccessor connectionAccessor,
      Function<Connection, Statement> statementFunction,
      Function<Result, Publisher<R>> resultMappingFunction) {
    this.sql = sql;
    this.connectionAccessor = connectionAccessor;
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
    return this.connectionAccessor.inConnectionMany(
        connection ->
            Flux.from(statementFunction.apply(connection).execute())
                .flatMap(resultMappingFunction));
  }

  @Override
  public Mono<Integer> rowsUpdated() {
    return this.connectionAccessor.inConnection(
        connection ->
            Mono.from(statementFunction.apply(connection).execute())
                .flatMap(result -> Mono.from(result.getRowsUpdated())));
  }
}
