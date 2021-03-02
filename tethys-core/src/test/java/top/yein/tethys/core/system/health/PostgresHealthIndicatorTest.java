package top.yein.tethys.core.system.health;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.test.StepVerifier;
import top.yein.tethys.system.health.HealthStatus;

/**
 * {@link PostgresHealthIndicator} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class PostgresHealthIndicatorTest {

  String defaultR2dbcUrl = "r2dbc:postgresql://postgres:123456@127.0.0.1:5432/tethys";

  String r2dbcUrl() {
    return Optional.ofNullable(System.getenv("TETHYS_R2DBC_URL")).orElse(defaultR2dbcUrl);
  }

  @Test
  void health() {
    var connectionFactory = ConnectionFactories.get(r2dbcUrl());
    var indicator = new PostgresHealthIndicator(connectionFactory);
    var p = indicator.health();
    StepVerifier.create(p)
        .expectNextMatches(health -> health.getStatus() == HealthStatus.UP)
        .expectComplete()
        .verify();
  }

  @ValueSource(
      strings = {
        // IP 地址错误
        "r2dbc:postgresql://postgres:123456@199.199.199.199:5432/tethys",
      })
  @ParameterizedTest
  void healthDown(String url) {
    var options =
        ConnectionFactoryOptions.builder()
            .from(ConnectionFactoryOptions.parse(url))
            .option(ConnectionFactoryOptions.CONNECT_TIMEOUT, Duration.ofSeconds(1))
            .build();
    var connectionFactory = ConnectionFactories.get(options);
    var indicator = new PostgresHealthIndicator(connectionFactory);
    var p = indicator.health();
    StepVerifier.create(p)
        .expectNextMatches(health -> health.getStatus() == HealthStatus.DOWN)
        .expectComplete()
        .verify();
  }
}
