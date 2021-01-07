/*
 * Copyright 2019-2020 the original author or authors
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
package top.yein.tethys.im.main;

import static com.google.inject.Scopes.SINGLETON;
import static top.yein.tethys.packet.Namespaces.NS_GROUP_MESSAGE;
import static top.yein.tethys.packet.Namespaces.NS_GROUP_SUBSCRIBE;
import static top.yein.tethys.packet.Namespaces.NS_GROUP_UNSUBSCRIBE;
import static top.yein.tethys.packet.Namespaces.NS_PING;
import static top.yein.tethys.packet.Namespaces.NS_PRIVATE_MESSAGE;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import javax.crypto.SecretKey;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.ConfigKeys;
import top.yein.tethys.core.auth.JwsAuthService;
import top.yein.tethys.core.auth.TokenServiceImpl;
import top.yein.tethys.core.resource.TokenResource;
import top.yein.tethys.core.session.DefaultSessionGroupManager;
import top.yein.tethys.core.session.DefaultSessionManager;
import top.yein.tethys.core.session.LocalSessionIdGenerator;
import top.yein.tethys.im.handler.GroupMessageHandler;
import top.yein.tethys.im.handler.GroupSubscribeHandler;
import top.yein.tethys.im.handler.GroupUnsubscribeHandler;
import top.yein.tethys.im.handler.PingHandler;
import top.yein.tethys.im.handler.PrivateMessageHandler;
import top.yein.tethys.im.server.ImServer;
import top.yein.tethys.im.server.PacketDispatcher;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.im.server.RestRegister;
import top.yein.tethys.im.server.WebsocketHandler;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.session.SessionIdGenerator;
import top.yein.tethys.session.SessionManager;

/**
 * Guice IM 模块.
 *
 * @author KK (kzou227@qq.com)
 */
public final class ImGuiceModule extends AbstractModule {

  private final Config config;

  public ImGuiceModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    // 令牌
    bind(TokenResource.class).in(SINGLETON);

    bind(RestRegister.class).in(SINGLETON);
    bind(WebsocketHandler.class).in(SINGLETON);
    bind(SessionManager.class).to(DefaultSessionManager.class).in(SINGLETON);

    // 消息处理总线
    bind(PacketDispatcher.class).in(SINGLETON);
    binderPacketHandler();
  }

  @Provides
  @Singleton
  public SessionIdGenerator sessionIdGenerator() {
    return new LocalSessionIdGenerator();
  }

  @Provides
  @Singleton
  public SessionGroupManager sessionGroupManager() {
    return new DefaultSessionGroupManager();
  }

  @Provides
  @Singleton
  public TokenService tokenService() {
    return new TokenServiceImpl(jwtSecrets());
  }

  @Provides
  @Singleton
  public AuthService authService() {
    // var enabled = config.getBoolean(ConfigKeys.IM_SERVER_ENABLED_ANONYMOUS);
    return new JwsAuthService(jwtSecrets());
  }

  @Provides
  @Singleton
  public ImServer imServer(WebsocketHandler websocketHandler, RestRegister restRegister) {
    return new ImServer(
        config.getString(ConfigKeys.IM_SERVER_ADDR), websocketHandler, restRegister);
  }

  private void binderPacketHandler() {
    var handlerBinder = MapBinder.newMapBinder(binder(), String.class, PacketHandler.class);
    handlerBinder.addBinding(NS_PING).to(PingHandler.class).in(SINGLETON);
    handlerBinder.addBinding(NS_PRIVATE_MESSAGE).to(PrivateMessageHandler.class).in(SINGLETON);
    handlerBinder.addBinding(NS_GROUP_MESSAGE).to(GroupMessageHandler.class).in(SINGLETON);
    handlerBinder.addBinding(NS_GROUP_SUBSCRIBE).to(GroupSubscribeHandler.class).in(SINGLETON);
    handlerBinder.addBinding(NS_GROUP_UNSUBSCRIBE).to(GroupUnsubscribeHandler.class).in(SINGLETON);
  }

  private Map<String, SecretKey> jwtSecrets() {
    var builder = ImmutableMap.<String, SecretKey>builder();
    for (Entry<String, ConfigValue> e : config.getObject(ConfigKeys.JWT_SECRETS).entrySet()) {
      String v = (String) e.getValue().unwrapped();
      var sk = Keys.hmacShaKeyFor(v.getBytes(StandardCharsets.UTF_8));
      builder.put(e.getKey(), sk);
    }
    return builder.build();
  }
}
