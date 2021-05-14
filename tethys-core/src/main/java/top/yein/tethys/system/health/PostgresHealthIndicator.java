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
package top.yein.tethys.system.health;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.system.health.Health.Builder;

/**
 * PostgreSQL 数据库健康状况.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class PostgresHealthIndicator implements HealthIndicator {

  private static final String CHECK_SQL = "SELECT CURRENT_SETTING('SERVER_VERSION')";

  private final String componentName;
  private final ConnectionFactory connectionFactory;

  /**
   * 使用数据客户端创建实例.
   *
   * @param connectionFactory 数据库客户端
   */
  @Inject
  public PostgresHealthIndicator(ConnectionFactory connectionFactory) {
    this("PostgreSQL", connectionFactory);
  }

  /**
   * 使用组件名称与数据客户端创建实例.
   *
   * @param componentName 组件名称
   * @param connectionFactory 数据库客户端
   */
  public PostgresHealthIndicator(String componentName, ConnectionFactory connectionFactory) {
    this.componentName = componentName;
    this.connectionFactory = connectionFactory;
  }

  @Override
  public Mono<Health> health() {
    return Flux.usingWhen(
            connectionFactory.create(),
            connection ->
                Flux.from(connection.createStatement(CHECK_SQL).execute())
                    .flatMap(result -> result.map((row, rowMetadata) -> row.get(0, String.class))),
            Connection::close)
        .last()
        .map(version -> new Builder(componentName).up().withDetail("version", version).build())
        .onErrorResume(
            ex -> {
              log.error("数据库健康状况异常 {}", connectionFactory, ex);
              return Mono.just(new Health.Builder(componentName).down(ex).build());
            });
  }
}
