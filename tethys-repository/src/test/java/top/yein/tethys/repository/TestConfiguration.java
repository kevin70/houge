package top.yein.tethys.repository;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;

/**
 * Spring 测试配置.
 *
 * @author KK (kzou227@qq.com)
 */
@Configuration(proxyBeanMethods = false)
public class TestConfiguration {

  @Bean
  public ConnectionFactory connectionFactory() {
    //    return ConnectionFactories.get(r2dbcUrl);
    return ConnectionFactories.get(
        "r2dbc:postgresql://postgres:hellohuixin@192.168.1.106:5432/tethys");
  }

  @Bean
  public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
    var transactionManager = new R2dbcTransactionManager(connectionFactory);
    return transactionManager;
  }

  @Bean
  public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
    return DatabaseClient.builder().connectionFactory(connectionFactory).build();
  }
}
