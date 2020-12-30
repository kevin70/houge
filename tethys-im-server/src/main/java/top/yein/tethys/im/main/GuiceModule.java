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

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import javax.crypto.SecretKey;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.ConfigKeys;
import top.yein.tethys.core.auth.JwsAuthService;
import top.yein.tethys.core.resource.TokenResource;
import top.yein.tethys.core.session.DefaultSessionGroupManager;
import top.yein.tethys.core.session.DefaultSessionIdGenerator;
import top.yein.tethys.core.session.DefaultSessionManager;
import top.yein.tethys.im.handler.BasisPacketHandler;
import top.yein.tethys.im.server.ImServer;
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
public final class GuiceModule extends AbstractModule {

  private final Config config;

  public GuiceModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(TokenResource.class).in(Scopes.SINGLETON);
    bind(RestRegister.class).in(Scopes.SINGLETON);

    bind(WebsocketHandler.class).in(Scopes.SINGLETON);
  }

  @Provides
  @Singleton
  public SessionIdGenerator sessionIdGenerator() {
    return new DefaultSessionIdGenerator();
  }

  @Provides
  @Singleton
  public SessionManager sessionManager() {
    return new DefaultSessionManager();
  }

  @Provides
  @Singleton
  public SessionGroupManager sessionGroupManager() {
    return new DefaultSessionGroupManager();
  }

  @Provides
  @Singleton
  public AuthService authService() {
    var enabled = config.getBoolean(ConfigKeys.IM_SERVER_ENABLED_ANONYMOUS);
    var builder = ImmutableMap.<String, SecretKey>builder();
    for (Entry<String, ConfigValue> e : config.getObject(ConfigKeys.JWT_SECRETS).entrySet()) {
      String v = (String) e.getValue().unwrapped();
      var sk = Keys.hmacShaKeyFor(v.getBytes(StandardCharsets.UTF_8));
      builder.put(e.getKey(), sk);
    }

    return new JwsAuthService(enabled, builder.build());
  }

  @Provides
  @Singleton
  public PacketHandler packetHandler(
      SessionManager sessionManager, SessionGroupManager sessionGroupManager) {
    return new BasisPacketHandler(sessionManager, sessionGroupManager);
  }

  @Provides
  @Singleton
  public ImServer imServer(WebsocketHandler websocketHandler, RestRegister restRegister) {
    return new ImServer(
        config.getString(ConfigKeys.IM_SERVER_ADDR), websocketHandler, restRegister);
  }
}
