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
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import top.yein.tethys.r2dbc.TransactionOperator;

/**
 * 事务.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class TransactionOperatorImpl implements TransactionOperator {

  private final ConnectionFactory connectionFactory;

  /**
   * 使用 R2DBC 连接工厂构建对象.
   *
   * @param connectionFactory R2DBC 连接工厂
   */
  public TransactionOperatorImpl(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public <T> Flux<T> transactional(Flux<T> flux) {
    return Flux.usingWhen(
        connectionFactory.create(),
        connection -> {
          log.debug("开户事务 {}", connection);
          return Flux.from(connection.beginTransaction())
              .thenMany(Flux.defer(() -> flux))
              .delayUntil(
                  unused -> {
                    log.debug("提交事务 {}", connection);
                    return connection.commitTransaction();
                  })
              .onErrorResume(
                  ex -> {
                    log.debug("回滚事务 {}", connection);
                    return Mono.from(connection.rollbackTransaction()).then(Mono.error(ex));
                  })
              .contextWrite(Context.of(Connection.class, connection));
        },
        Connection::close,
        (it, err) -> it.close(),
        Connection::close);
  }

  @Override
  public <T> Mono<T> transactional(Mono<T> mono) {
    return transactional(Flux.from(mono)).last();
  }
}
