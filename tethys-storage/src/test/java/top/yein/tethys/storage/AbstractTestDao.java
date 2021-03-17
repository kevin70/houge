package top.yein.tethys.storage;

import com.typesafe.config.ConfigFactory;
import io.r2dbc.spi.ConnectionFactories;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.core.r2dbc.DefaultR2dbcClient;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * 测试存储的基类.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public abstract class AbstractTestDao {

  protected static R2dbcClient r2dbcClient;

  @BeforeAll
  static void setUp() {
    var config = ConfigFactory.parseResources("tethys-test.conf");
    log.debug("单元测试配置\n{}", config.root().render());
    var connectionFactory =
        ConnectionFactories.get(config.getString(ConfigKeys.MESSAGE_STORAGE_R2DBC_URL));
    r2dbcClient = new DefaultR2dbcClient(connectionFactory);
  }

  /**
   * @param sql
   * @param parameters
   */
  protected void clean(String sql, Object[] parameters) {
    r2dbcClient.sql(sql).bind(parameters).rowsUpdated().block();
  }
}
