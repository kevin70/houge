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

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;
import top.yein.tethys.r2dbc.R2dbcClient.BatchExecuteSpec;

/**
 * 默认批量执行规范实现.
 *
 * @author KK (kzou227@qq.com)
 */
class DefaultBatchExecuteSpec implements BatchExecuteSpec {

  final ConnectionFactory connectionFactory;
  final String sql;

  DefaultBatchExecuteSpec(ConnectionFactory connectionFactory, String sql) {
    this.connectionFactory = connectionFactory;
    this.sql = sql;
  }

  @Override
  public Mono<Integer> rowsUpdated() {
    return Mono.from(connectionFactory.create())
        .flatMap(
            connection ->
                Mono.from(connection.createBatch().add(sql).execute())
                    .flatMap(result -> Mono.from(result.getRowsUpdated())));
  }
}
