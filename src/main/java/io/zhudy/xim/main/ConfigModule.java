/**
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
package io.zhudy.xim.main;

import static com.google.inject.name.Names.named;
import static io.zhudy.xim.ConfigKeys.IM_SERVER_ADDR;
import static io.zhudy.xim.ConfigKeys.IM_SERVER_AUTH_JWT_SECRETS;
import static io.zhudy.xim.ConfigKeys.IM_SERVER_ENABLED_ANONYMOUS;

import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.typesafe.config.ConfigFactory;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import lombok.extern.log4j.Log4j2;

/**
 * 应用配置加载 Guice 模块.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class ConfigModule extends AbstractModule {

  private final String configResource;

  /**
   * 默认构建函数.
   *
   * <p>默认加载 {@code classpath:xim.conf} 配置文件.
   */
  public ConfigModule() {
    this("xim.conf");
  }

  /**
   * 指定加载的配置文件.
   *
   * @param configResource CLASSPATH 下配置资源
   */
  public ConfigModule(String configResource) {
    this.configResource = configResource;
  }

  @Override
  protected void configure() {
    var config = ConfigFactory.parseResources(this.configResource).resolve();
    log.info(
        "------------------------------ XIM CONFIG ------------------------------\n{}\n",
        config.root().render());

    // 校验并绑定配置
    {
      var k = IM_SERVER_ADDR;
      var v = config.getString(k);
      bind(HostAndPort.class).annotatedWith(named(k)).toInstance(HostAndPort.fromString(v));
    }

    {
      var k = IM_SERVER_ENABLED_ANONYMOUS;
      bindConstant().annotatedWith(named(k)).to(config.getBoolean(k));
    }

    {
      var k = IM_SERVER_AUTH_JWT_SECRETS;
      var mapBinder = MapBinder.newMapBinder(binder(), String.class, Key.class, named(k));
      config
          .getObject(k)
          .entrySet()
          .forEach(
              e -> {
                String v = (String) e.getValue().unwrapped();
                var sk = Keys.hmacShaKeyFor(v.getBytes());
                mapBinder.addBinding(e.getKey()).toInstance(sk);
              });
    }
  }
}
