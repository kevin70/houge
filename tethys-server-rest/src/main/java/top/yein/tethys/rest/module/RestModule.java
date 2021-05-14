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
package top.yein.tethys.rest.module;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import java.util.Map.Entry;
import javax.inject.Singleton;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.auth.impl.JwsAuthService;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.id.YeinGidMessageIdGenerator;
import top.yein.tethys.rest.RestApplicationIdentifier;
import top.yein.tethys.rest.http.Interceptors;
import top.yein.tethys.rest.http.RoutingService;
import top.yein.tethys.rest.resource.AuthInterceptor;
import top.yein.tethys.rest.resource.ServiceAuthInterceptor;
import top.yein.tethys.rest.resource.i.GroupResource;
import top.yein.tethys.rest.resource.i.TokenResource;
import top.yein.tethys.rest.resource.i.UserResource;
import top.yein.tethys.rest.resource.p.MessageIdResource;
import top.yein.tethys.rest.resource.p.MessageResource;
import top.yein.tethys.system.identifier.ApplicationIdentifier;

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
    bind(AuthService.class).to(JwsAuthService.class);
    bind(TokenService.class).to(JwsAuthService.class);

    // 绑定 Web 访问资源对象
    bind(AuthInterceptor.class).in(Scopes.SINGLETON);
    this.bindResources();
  }

  @Provides
  @Singleton
  public Interceptors interceptors(AuthInterceptor authInterceptor) {
    return new Interceptors(authInterceptor::handle, serviceAuthInterceptor()::handle);
  }

  private void bindResources() {
    var binder = Multibinder.newSetBinder(binder(), RoutingService.class);
    binder.addBinding().to(MessageIdResource.class).in(Scopes.SINGLETON);
    binder.addBinding().to(MessageResource.class).in(Scopes.SINGLETON);
    binder.addBinding().to(GroupResource.class).in(Scopes.SINGLETON);
    binder.addBinding().to(UserResource.class).in(Scopes.SINGLETON);
    binder.addBinding().to(TokenResource.class).in(Scopes.SINGLETON);
  }

  private ServiceAuthInterceptor serviceAuthInterceptor() {
    var basicUsersBuilder = ImmutableMap.<String, String>builder();
    if (config.hasPath(ConfigKeys.SERVICE_AUTH_BASIC)) {
      for (Entry<String, ConfigValue> entry :
          config.getConfig(ConfigKeys.SERVICE_AUTH_BASIC).entrySet()) {
        basicUsersBuilder.put(entry.getKey(), entry.getValue().unwrapped().toString());
      }
    }
    return new ServiceAuthInterceptor(basicUsersBuilder.build());
  }
}
