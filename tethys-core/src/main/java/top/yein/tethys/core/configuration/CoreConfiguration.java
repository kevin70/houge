package top.yein.tethys.core.configuration;

import com.google.common.base.Strings;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.r2dbc.spi.ConnectionFactory;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.util.unit.DataSize;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.core.MessageProperties;
import top.yein.tethys.core.session.LocalSessionIdGenerator;
import top.yein.tethys.core.system.health.DiskSpaceHealthIndicator;
import top.yein.tethys.core.system.health.HealthServiceImpl;
import top.yein.tethys.core.system.health.PostgresHealthIndicator;
import top.yein.tethys.core.system.info.AppInfoContributor;
import top.yein.tethys.core.system.info.InfoServiceImpl;
import top.yein.tethys.core.system.info.JavaInfoContributor;
import top.yein.tethys.session.SessionIdGenerator;
import top.yein.tethys.system.health.HealthService;
import top.yein.tethys.system.info.InfoService;

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
    configurer.setFileEncoding(StandardCharsets.UTF_8.name());
    return configurer;
  }

  /**
   * @param prometheusMeterRegistry
   * @return
   */
  @Bean
  public JvmGcMetrics jvmGcMetrics(PrometheusMeterRegistry prometheusMeterRegistry) {
    var metrics = new JvmGcMetrics();
    metrics.bindTo(prometheusMeterRegistry);
    return metrics;
  }

  /**
   * @param prometheusMeterRegistry
   * @return
   */
  @Bean
  public JvmHeapPressureMetrics jvmHeapPressureMetrics(
      PrometheusMeterRegistry prometheusMeterRegistry) {
    var metrics = new JvmHeapPressureMetrics();
    metrics.bindTo(prometheusMeterRegistry);
    return metrics;
  }

  /**
   * @param identifier
   * @return
   */
  @Bean
  public PrometheusMeterRegistry prometheusMeterRegistry(ApplicationIdentifier identifier) {
    var prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    var config = prometheusRegistry.config();
    config.commonTags(
        "application",
        Strings.lenientFormat("%s-%s", identifier.applicationName(), identifier.version()),
        "fid",
        String.valueOf(identifier.fid()));

    // 绑定监控
    new UptimeMetrics().bindTo(prometheusRegistry);
    new JvmMemoryMetrics().bindTo(prometheusRegistry);
    return prometheusRegistry;
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

  /**
   * @param applicationIdentifier
   * @return
   */
  @Bean
  public InfoService infoService(ApplicationIdentifier applicationIdentifier) {
    // FIXME 后期完善
    var contributors =
        List.of(new AppInfoContributor(applicationIdentifier), new JavaInfoContributor());
    return new InfoServiceImpl(contributors);
  }
}
