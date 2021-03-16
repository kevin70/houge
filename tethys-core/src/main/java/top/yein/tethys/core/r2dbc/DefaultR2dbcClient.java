package top.yein.tethys.core.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import java.util.Objects;
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
    return new DefaultExecuteSpec(connectionFactory, sql);
  }
}
