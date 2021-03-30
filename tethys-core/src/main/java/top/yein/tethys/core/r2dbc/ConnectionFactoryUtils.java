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
package top.yein.tethys.core.r2dbc;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;

/**
 * R2DBC {@link io.r2dbc.spi.ConnectionFactory} 工具类.
 *
 * @author KK (kzou227@qq.com)
 */
public final class ConnectionFactoryUtils {

  private ConnectionFactoryUtils() {}

  /**
   * @param connectionFactory
   * @return
   */
  public static Mono<Connection> getConnection(ConnectionFactory connectionFactory) {
    return Mono.deferContextual(
        contextView -> {
          if (contextView.hasKey(Connection.class)) {
            return Mono.just(contextView.get(Connection.class));
          }
          return Mono.from(connectionFactory.create());
        });
  }

  /**
   * 判断当前上下文是否存在事务.
   *
   * @return
   */
  public static Mono<Boolean> hasContextConnection() {
    return Mono.deferContextual(contextView -> Mono.just(contextView.hasKey(Connection.class)));
  }
}
