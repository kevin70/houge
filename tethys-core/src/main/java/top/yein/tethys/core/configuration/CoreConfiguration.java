package top.yein.tethys.core.configuration;

import com.google.common.base.Charsets;
import io.r2dbc.spi.ConnectionFactory;
import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.util.unit.DataSize;
import top.yein.tethys.core.MessageProperties;
import top.yein.tethys.core.session.LocalSessionIdGenerator;
import top.yein.tethys.core.system.health.DiskSpaceHealthIndicator;
import top.yein.tethys.core.system.health.PostgresHealthIndicator;
import top.yein.tethys.core.system.service.HealthServiceImpl;
import top.yein.tethys.session.SessionIdGenerator;
import top.yein.tethys.system.health.HealthService;

/** @author KK (kzou227@qq.com) */
@Configuration(proxyBeanMethods = false)
public class CoreConfiguration {

  /** @return */
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    var configurer = new PropertySourcesPlaceholderConfigurer();
    configurer.setLocations(
        new ClassPathResource("tethys.properties"),
        new PathResource("/opt/tethys.properties"),
        new PathResource("/etc/tethys/tethys.properties"),
        new PathResource("tethys-dev.properties"));
    configurer.setIgnoreResourceNotFound(true);
    configurer.setFileEncoding(Charsets.UTF_8.name());
    return configurer;
  }

  /**
   * @param connectionFactory
   * @return
   */
  @Bean
  public R2dbcTransactionManager transactionManager(ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

  /**
   * @param autofillId
   * @return
   */
  @Bean
  public MessageProperties messageProperties(
      @Value("${message.autofill.id:false}") boolean autofillId) {
    return new MessageProperties(autofillId);
  }

  /** @return */
  @Bean
  public SessionIdGenerator sessionIdGenerator() {
    return new LocalSessionIdGenerator();
  }

  /**
   * @param connectionFactory
   * @return
   */
  @Bean
  public HealthService healthService(ConnectionFactory connectionFactory) {
    // FIXME 后期完善
    var indicators =
        List.of(
            new DiskSpaceHealthIndicator(new File("").getAbsoluteFile(), DataSize.parse("300MB")),
            new PostgresHealthIndicator(connectionFactory));
    return new HealthServiceImpl(indicators);
  }
}
