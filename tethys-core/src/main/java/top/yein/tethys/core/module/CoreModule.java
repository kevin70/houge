/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import javax.inject.Singleton;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.auth.JwsAuthService;
import top.yein.tethys.core.auth.TokenServiceImpl;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.core.resource.AuthInterceptor;
import top.yein.tethys.core.system.health.HealthResource;
import top.yein.tethys.core.system.health.HealthServiceImpl;
import top.yein.tethys.core.system.health.PostgresHealthIndicator;
import top.yein.tethys.core.system.info.AppInfoContributor;
import top.yein.tethys.core.system.info.InfoResource;
import top.yein.tethys.core.system.info.InfoServiceImpl;
import top.yein.tethys.core.system.info.JavaInfoContributor;
import top.yein.tethys.system.health.HealthIndicator;
import top.yein.tethys.system.health.HealthService;
import top.yein.tethys.system.info.InfoContributor;
import top.yein.tethys.system.info.InfoService;

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

    // 应用信息
    bind(InfoService.class).to(InfoServiceImpl.class).in(Scopes.SINGLETON);
    var infoContributorsBinder = Multibinder.newSetBinder(binder(), InfoContributor.class);
    infoContributorsBinder.addBinding().to(AppInfoContributor.class).in(Scopes.SINGLETON);
    infoContributorsBinder.addBinding().to(JavaInfoContributor.class).in(Scopes.SINGLETON);

    // 健康状况
    bind(HealthService.class).to(HealthServiceImpl.class).in(Scopes.SINGLETON);
    var healthIndicatorsBinder = Multibinder.newSetBinder(binder(), HealthIndicator.class);
    healthIndicatorsBinder.addBinding().to(PostgresHealthIndicator.class).in(Scopes.SINGLETON);

    // resources
    bind(AuthInterceptor.class).in(Scopes.SINGLETON);
    var routingServicesBinder = Multibinder.newSetBinder(binder(), RoutingService.class);
    routingServicesBinder.addBinding().to(InfoResource.class).in(Scopes.SINGLETON);
    routingServicesBinder.addBinding().to(HealthResource.class).in(Scopes.SINGLETON);
  }

  @Provides
  @Singleton
  public PrometheusMeterRegistry prometheusMeterRegistry(ApplicationIdentifier identifier) {
    var prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    var registryConfig = prometheusRegistry.config();
    registryConfig.commonTags(
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
