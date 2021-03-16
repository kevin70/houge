package top.yein.tethys.core.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * {@link DefaultR2dbcClient} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class DefaultR2dbcClientTest {

  @Test
  void getConnectionFactory() {
    var connectionFactory = Mockito.mock(ConnectionFactory.class);
    var rc = new DefaultR2dbcClient(connectionFactory);
    assertThat(rc.getConnectionFactory()).isEqualTo(connectionFactory);
  }

  @Test
  void sql() {
    var connectionFactory = Mockito.mock(ConnectionFactory.class);
    var rc = new DefaultR2dbcClient(connectionFactory);
    var spec = rc.sql("select version()");
    assertThat(spec).isNotNull();
  }
}
