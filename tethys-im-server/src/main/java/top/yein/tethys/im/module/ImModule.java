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
package top.yein.tethys.im.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import javax.inject.Singleton;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.core.MessageProperties;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.id.YeinGidMessageIdGenerator;
import top.yein.tethys.core.resource.AuthInterceptor;
import top.yein.tethys.core.session.DefaultSessionGroupManager;
import top.yein.tethys.core.session.DefaultSessionManager;
import top.yein.tethys.core.session.LocalSessionIdGenerator;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.im.ImApplicationIdentifier;
import top.yein.tethys.im.handler.GroupMessageHandler;
import top.yein.tethys.im.handler.GroupSubscribeHandler;
import top.yein.tethys.im.handler.GroupUnsubscribeHandler;
import top.yein.tethys.im.handler.PingHandler;
import top.yein.tethys.im.handler.PrivateMessageHandler;
import top.yein.tethys.im.server.PacketDispatcher;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.im.server.WebsocketHandler;
import top.yein.tethys.packet.Namespaces;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.session.SessionIdGenerator;
import top.yein.tethys.session.SessionManager;

/**
 * IM Guice 模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class ImModule extends AbstractModule {

  private final Config config;

  public ImModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(ApplicationIdentifier.class).to(ImApplicationIdentifier.class).in(Scopes.SINGLETON);
    bind(WebsocketHandler.class).in(Scopes.SINGLETON);
    bind(PacketDispatcher.class).in(Scopes.SINGLETON);

    bind(SessionIdGenerator.class).to(LocalSessionIdGenerator.class).in(Scopes.SINGLETON);
    bind(SessionManager.class).to(DefaultSessionManager.class).in(Scopes.SINGLETON);
    bind(SessionGroupManager.class).to(DefaultSessionGroupManager.class).in(Scopes.SINGLETON);

    // 消息 ID 生成器
    bind(MessageIdGenerator.class).to(YeinGidMessageIdGenerator.class).in(Scopes.SINGLETON);

    // PacketHandlers =========================================>>>
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_PING))
        .to(PingHandler.class)
        .in(Scopes.SINGLETON);
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_PRIVATE_MESSAGE))
        .to(PrivateMessageHandler.class)
        .in(Scopes.SINGLETON);
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_GROUP_MESSAGE))
        .to(GroupMessageHandler.class)
        .in(Scopes.SINGLETON);
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_GROUP_SUBSCRIBE))
        .to(GroupSubscribeHandler.class)
        .in(Scopes.SINGLETON);
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_GROUP_UNSUBSCRIBE))
        .to(GroupUnsubscribeHandler.class)
        .in(Scopes.SINGLETON);
    // PacketHandlers =========================================<<<
  }

  @Provides
  @Singleton
  public MessageProperties messageProperties() {
    boolean autofillId =
        (boolean)
            config
                .getValue(ConfigKeys.MESSAGE_AUTOFILL_ID)
                .withFallback(ConfigValueFactory.fromAnyRef(false))
                .unwrapped();
    return new MessageProperties(autofillId);
  }

  @Provides
  @Singleton
  public Interceptors interceptors(AuthInterceptor authInterceptor) {
    return new Interceptors(authInterceptor::handle);
  }
}
