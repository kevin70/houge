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

/**
 * Tethys Spring 公共的基础配置.
 *
 * @author KK (kzou227@qq.com)
 */
@Configuration(proxyBeanMethods = false)
public class CoreConfiguration {

  /**
   * 创建 Tethys Spring 基础配置对象.
   *
   * <p>配置文件按照如下的顺序加载, 如果有相同的配置项越后加载的配置将覆盖先前的配置内容.
   *
   * <ul>
   *   <li>classpath:tethys.properties
   *   <li>file:/opt/tethys.properties
   *   <li>file:/etc/tethys/tethys.properties
   *   <li>file:tethys-dev.properties
   * </ul>
   *
   * @return Spring 配置对象
   */
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
   * 创建 JVM GC 度量指标对象并与 {@link PrometheusMeterRegistry} 绑定.
   *
   * @param prometheusMeterRegistry prometheus 注册表
   * @return JVM GC 度量指标对象
   */
  @Bean
  public JvmGcMetrics jvmGcMetrics(PrometheusMeterRegistry prometheusMeterRegistry) {
    var metrics = new JvmGcMetrics();
    metrics.bindTo(prometheusMeterRegistry);
    return metrics;
  }

  /**
   * 创建 JVM 堆内存压力度量指示对象并与 {@link PrometheusMeterRegistry} 绑定.
   *
   * @param prometheusMeterRegistry prometheus 注册表
   * @return JVM 堆内存压力度量指标对象
   */
  @Bean
  public JvmHeapPressureMetrics jvmHeapPressureMetrics(
      PrometheusMeterRegistry prometheusMeterRegistry) {
    var metrics = new JvmHeapPressureMetrics();
    metrics.bindTo(prometheusMeterRegistry);
    return metrics;
  }

  /**
   * 创建 Prometheus 注册表对象.
   *
   * <p>默认绑定的度量指标:
   *
   * <ul>
   *   <li>{@link UptimeMetrics}
   *   <li>{@link JvmMemoryMetrics}
   * </ul>
   *
   * @param identifier 应用程序标识
   * @return Prometheus 注册表对象
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
   * 创建 R2DBC 事务管理对象.
   *
   * @param connectionFactory 数据库连接工厂
   * @return R2DBC 事务管理对象
   */
  @Bean
  public R2dbcTransactionManager transactionManager(ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

  /**
   * 创建消息配置对象.
   *
   * @param autofillId 是否开启自动填充消息 ID
   * @return 消息配置对象
   */
  @Bean
  public MessageProperties messageProperties(
      @Value("${message.autofill.id:false}") boolean autofillId) {
    return new MessageProperties(autofillId);
  }

  /**
   * 创建会话 ID 生成器对象.
   *
   * @return 会话 ID 生成器
   */
  @Bean
  public SessionIdGenerator sessionIdGenerator() {
    return new LocalSessionIdGenerator();
  }

  /**
   * 创建应用健康状况服务对象.
   *
   * @param connectionFactory 数据库连接
   * @return 应用健康状况服务对象
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
   * 创建应用信息服务对象.
   *
   * @param applicationIdentifier 应用程序标识对象
   * @return 应用信息服务对象
   */
  @Bean
  public InfoService infoService(ApplicationIdentifier applicationIdentifier) {
    // FIXME 后期完善
    var contributors =
        List.of(new AppInfoContributor(applicationIdentifier), new JavaInfoContributor());
    return new InfoServiceImpl(contributors);
  }
}
