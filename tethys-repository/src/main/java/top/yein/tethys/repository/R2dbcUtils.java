package top.yein.tethys.repository;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;

/**
 * R2DBC 工具类.
 *
 * @author KK (kzou227@qq.com)
 */
public final class R2dbcUtils {

  // TODO: 后期完善事务管理

  /**
   * 获取 R2DBC 链接.
   *
   * @return 链接
   */
  public static Mono<Connection> getConnection() {
    return Mono.deferContextual(
        contextView -> {
          if (contextView.hasKey(Connection.class)) {
            return Mono.just(contextView.get(Connection.class));
          }

          if (contextView.hasKey(ConnectionFactory.class)) {
            var connectionFactory = contextView.get(ConnectionFactory.class);
            return Mono.from(connectionFactory.create());
          }

          throw new IllegalStateException("No r2dbc connection");
        });
  }
}
