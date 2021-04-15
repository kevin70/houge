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
package top.yein.tethys.core.r2dbc;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.R2dbcException;
import java.util.Objects;
import java.util.function.Function;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * 默认实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class DefaultR2dbcClient implements R2dbcClient {

  private final ConnectionFactory connectionFactory;

  /**
   * 使用 R2DBC 创建连接工厂构建实例.
   *
   * @param connectionFactory R2DBC 创建连接工厂
   */
  public DefaultR2dbcClient(ConnectionFactory connectionFactory) {
    Objects.requireNonNull(connectionFactory, "[connectionFactory]不能为 NULL");
    this.connectionFactory = connectionFactory;
  }

  @Override
  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  @Override
  public ExecuteSpec sql(String sql) {
    return new DefaultExecuteSpec(this, sql);
  }

  @Override
  public BatchExecuteSpec batchSql(String sql) {
    return new DefaultBatchExecuteSpec(this, sql);
  }

  @Override
  public <T> Mono<T> inConnection(Function<Connection, Mono<T>> action) {
    Mono<Connection> connectionMono = ConnectionFactoryUtils.getConnection(connectionFactory);
    return Mono.usingWhen(
        connectionMono,
        connection -> {
          try {
            return action.apply(connection);
          } catch (R2dbcException e) {
            return Mono.error(e);
          }
        },
        this::closeConnection,
        (it, err) -> it.close(),
        this::closeConnection);
  }

  @Override
  public <T> Flux<T> inConnectionMany(Function<Connection, Flux<T>> action) {
    Mono<Connection> connectionMono = ConnectionFactoryUtils.getConnection(connectionFactory);
    return Flux.usingWhen(
        connectionMono,
        connection -> {
          try {
            return action.apply(connection);
          } catch (R2dbcException e) {
            return Mono.error(e);
          }
        },
        this::closeConnection,
        (it, err) -> it.close(),
        this::closeConnection);
  }

  private Mono<Void> closeConnection(Connection connection) {
    return Mono.defer(
        () ->
            ConnectionFactoryUtils.hasContextConnection()
                .filter(it -> !it)
                .delayUntil(unused -> connection.close())
                .then());
  }
}
