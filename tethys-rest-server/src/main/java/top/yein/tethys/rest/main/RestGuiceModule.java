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
package top.yein.tethys.rest.main;

import static com.google.inject.Scopes.SINGLETON;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import javax.crypto.SecretKey;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.auth.TokenService;
import top.yein.tethys.core.auth.JwsAuthService;
import top.yein.tethys.core.auth.TokenServiceImpl;
import top.yein.tethys.core.id.YeinGidMessageIdGenerator;
import top.yein.tethys.core.resource.AuthenticationInterceptor;
import top.yein.tethys.core.resource.TokenResource;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.rest.resource.MessageIdResource;
import top.yein.tethys.rest.server.CustomRouters;
import top.yein.tethys.rest.server.RestServer;

/**
 * Guice 模块.
 *
 * @author KK (kzou227@qq.com)
 */
public final class RestGuiceModule extends AbstractModule {

  private final Config config;

  public RestGuiceModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(MessageIdGenerator.class).to(YeinGidMessageIdGenerator.class).in(SINGLETON);

    // resources
    bind(TokenResource.class).in(SINGLETON);
    bind(MessageIdResource.class).in(SINGLETON);

    bind(AuthenticationInterceptor.class).in(SINGLETON);
    bind(CustomRouters.class).in(SINGLETON);
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
  public RestServer restServer(CustomRouters customRouters) {
    return new RestServer(config.getString(ConfigKeys.REST_SERVER_ADDR), customRouters);
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
