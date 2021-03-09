package top.yein.tethys.repository;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

/**
 * Spring 数据存储配置.
 *
 * @author KK (kzou227@qq.com)
 */
@Configuration(proxyBeanMethods = false)
public class RepositoryConfiguration {

  /**
   * 使用R2DBC URL创建数据库连接工厂对象.
   *
   * <p>URL默认获取Spring配置{@code ${message-storage.r2dbc.url}}.
   *
   * @param url R2DBC URL
   * @return 数据库连接工厂
   */
  @Bean
  public ConnectionFactory connectionFactory(@Value("${message-storage.r2dbc.url}") String url) {
    return ConnectionFactories.get(url);
  }

  /**
   * 使用数据库连接工厂创建Spring R2DBC客户端.
   *
   * @param connectionFactory 数据库连接工厂
   * @return Spring R2DBC客户端
   */
  @Bean
  public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
    return DatabaseClient.create(connectionFactory);
  }
}
