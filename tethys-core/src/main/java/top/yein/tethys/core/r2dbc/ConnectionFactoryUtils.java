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
   * 获取 {@link Connection}.
   *
   * <p>优先从 {@link reactor.util.context.Context} 获取连接返回, 如果上下文不存在 {@link Connection} 则通过 {@link
   * ConnectionFactory#create()} 返回连接.
   *
   * @param connectionFactory R2DBC 连接工厂
   * @return {@link Connection}
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
   * 判断 {@link reactor.util.context.Context} 是否存在 {@link Connection}.
   *
   * @return 存在则返回 {@code true} 反之返回 {@code false}
   */
  public static Mono<Boolean> hasContextConnection() {
    return Mono.deferContextual(contextView -> Mono.just(contextView.hasKey(Connection.class)));
  }
}
