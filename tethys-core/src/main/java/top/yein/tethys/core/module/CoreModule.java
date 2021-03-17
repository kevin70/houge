package top.yein.tethys.core.module;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.typesafe.config.Config;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.auth.JwsAuthService;
import top.yein.tethys.core.auth.TokenServiceImpl;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.core.resource.AuthInterceptor;
import top.yein.tethys.core.resource.TokenResource;

/**
 * Tethys Guice 基础模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class CoreModule extends AbstractModule {

  private final Config config;

  /**
   * 使用应用配置构建对象.
   *
   * @param config 应用配置
   */
  public CoreModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(AuthService.class).to(JwsAuthService.class).in(Scopes.SINGLETON);
    bind(TokenService.class).to(TokenServiceImpl.class).in(Scopes.SINGLETON);

    bind(AuthInterceptor.class).in(Scopes.SINGLETON);
    var routingServicesBinder = Multibinder.newSetBinder(binder(), RoutingService.class);
    routingServicesBinder.addBinding().to(TokenResource.class).in(Scopes.SINGLETON);
    //    bind(RoutingService.class).to(TokenResource.class).in(Scopes.SINGLETON);
  }

  @Provides
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
}
