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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import javax.inject.Singleton;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.core.id.YeinGidMessageIdGenerator;
import top.yein.tethys.core.resource.AuthInterceptor;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.rest.RestApplicationIdentifier;
import top.yein.tethys.rest.resource.i.GroupResource;
import top.yein.tethys.rest.resource.p.MessageIdResource;
import top.yein.tethys.rest.resource.p.MessageResource;

/**
 * REST Guice 模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class RestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ApplicationIdentifier.class).to(RestApplicationIdentifier.class).in(Scopes.SINGLETON);

    // 消息 ID 生成器
    bind(MessageIdGenerator.class).to(YeinGidMessageIdGenerator.class).in(Scopes.SINGLETON);

    // rest
    var routingServicesBinder = Multibinder.newSetBinder(binder(), RoutingService.class);
    routingServicesBinder.addBinding().to(MessageIdResource.class).in(Scopes.SINGLETON);
    routingServicesBinder.addBinding().to(MessageResource.class).in(Scopes.SINGLETON);
    routingServicesBinder.addBinding().to(GroupResource.class).in(Scopes.SINGLETON);
  }

  @Provides
  @Singleton
  public Interceptors interceptors(AuthInterceptor authInterceptor) {
    return new Interceptors(authInterceptor::handle);
  }
}
