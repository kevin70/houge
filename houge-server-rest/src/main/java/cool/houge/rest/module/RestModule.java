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
package cool.houge.rest.module;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import cool.houge.ConfigKeys;
import cool.houge.auth.AuthService;
import cool.houge.auth.TokenService;
import cool.houge.auth.impl.JwsAuthService;
import cool.houge.id.MessageIdGenerator;
import cool.houge.id.YeinGidMessageIdGenerator;
import cool.houge.rest.RestApplicationIdentifier;
import cool.houge.rest.controller.Interceptors;
import cool.houge.rest.controller.RoutingService;
import cool.houge.rest.controller.ServerAuthInterceptor;
import cool.houge.rest.controller.UserAuthInterceptor;
import cool.houge.rest.controller.group.GroupController;
import cool.houge.rest.controller.health.HealthController;
import cool.houge.rest.controller.info.InfoController;
import cool.houge.rest.controller.message.MessageController;
import cool.houge.rest.controller.message.MessageIdController;
import cool.houge.rest.controller.message.SendMessageController;
import cool.houge.rest.controller.token.TokenController;
import cool.houge.rest.controller.user.UserController;
import cool.houge.system.health.HealthIndicator;
import cool.houge.system.health.HealthService;
import cool.houge.system.health.HealthServiceImpl;
import cool.houge.system.health.PostgresHealthIndicator;
import cool.houge.system.identifier.ApplicationIdentifier;
import cool.houge.system.info.AppInfoContributor;
import cool.houge.system.info.InfoContributor;
import cool.houge.system.info.InfoService;
import cool.houge.system.info.InfoServiceImpl;
import cool.houge.system.info.JavaInfoContributor;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.inject.Singleton;

/**
 * REST Guice 模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class RestModule extends AbstractModule {

  private final Config config;

  /**
   * 使用应用配置构建对象.
   *
   * @param config 应用配置
   */
  public RestModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(ApplicationIdentifier.class).to(RestApplicationIdentifier.class).in(Scopes.SINGLETON);

    // 消息 ID 生成器
    bind(MessageIdGenerator.class).to(YeinGidMessageIdGenerator.class).in(Scopes.SINGLETON);

    // 访问认证及访问令牌
    bind(JwsAuthService.class).in(Scopes.SINGLETON);
    bind(AuthService.class).to(JwsAuthService.class).in(Scopes.SINGLETON);
    bind(TokenService.class).to(JwsAuthService.class).in(Scopes.SINGLETON);

    // 绑定 Web 访问资源对象
    bind(UserAuthInterceptor.class).in(Scopes.SINGLETON);
    this.bindResources();

    this.bindInfoService();
    this.bindHealthService();
  }

  @Provides
  @Singleton
  public Interceptors interceptors(UserAuthInterceptor userAuthInterceptor) {
    return new Interceptors(userAuthInterceptor::handle, serviceAuthInterceptor()::handle);
  }

  private void bindInfoService() {
    var binder = Multibinder.newSetBinder(binder(), InfoContributor.class);
    binder.addBinding().to(JavaInfoContributor.class).in(Scopes.SINGLETON);
    binder.addBinding().to(AppInfoContributor.class).in(Scopes.SINGLETON);

    bind(InfoService.class).to(InfoServiceImpl.class).in(Scopes.SINGLETON);
  }

  private void bindHealthService() {
    var binder = Multibinder.newSetBinder(binder(), HealthIndicator.class);
    Consumer<Class<? extends HealthIndicator>> co =
        clazz -> binder.addBinding().to(clazz).in(Scopes.SINGLETON);
    co.accept(PostgresHealthIndicator.class);

    bind(HealthService.class).to(HealthServiceImpl.class).in(Scopes.SINGLETON);
  }

  private void bindResources() {
    var binder = Multibinder.newSetBinder(binder(), RoutingService.class);
    Consumer<Class<? extends RoutingService>> b =
        clazz -> binder.addBinding().to(clazz).in(Scopes.SINGLETON);

    b.accept(InfoController.class);
    b.accept(HealthController.class);

    b.accept(MessageIdController.class);
    b.accept(MessageController.class);
    b.accept(SendMessageController.class);

    b.accept(GroupController.class);
    b.accept(UserController.class);
    b.accept(TokenController.class);
  }

  private ServerAuthInterceptor serviceAuthInterceptor() {
    var basicUsersBuilder = ImmutableMap.<String, String>builder();
    if (config.hasPath(ConfigKeys.SERVICE_AUTH_BASIC)) {
      for (Entry<String, ConfigValue> entry :
          config.getConfig(ConfigKeys.SERVICE_AUTH_BASIC).entrySet()) {
        basicUsersBuilder.put(entry.getKey(), entry.getValue().unwrapped().toString());
      }
    }
    return new ServerAuthInterceptor(basicUsersBuilder.build());
  }
}
