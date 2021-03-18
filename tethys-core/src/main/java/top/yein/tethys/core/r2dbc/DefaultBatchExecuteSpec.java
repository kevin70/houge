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
