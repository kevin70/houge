package top.yein.tethys.repository;

import com.typesafe.config.ConfigFactory;
import io.r2dbc.spi.ConnectionFactories;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.FetchSpec;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.core.r2dbc.DefaultR2dbcClient;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * 测试存储的基类.
 *
 * @author KK (kzou227@qq.com)
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath*:spring-test.xml")
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  DirtiesContextTestExecutionListener.class,
  TransactionalTestExecutionListener.class
})
@Log4j2
public abstract class AbstractTestRepository {

  @Autowired protected DatabaseClient dc;
  @Autowired protected ReactiveTransactionManager transactionManager;

  protected static R2dbcClient r2dbcClient;

  @BeforeAll
  static void setUp() {
    var config = ConfigFactory.parseResources("tethys-test.conf");
    log.debug("单元测试配置\n{}", config.root().render());
    var connectionFactory =
        ConnectionFactories.get(config.getString(ConfigKeys.MESSAGE_STORAGE_R2DBC_URL));
    r2dbcClient = new DefaultR2dbcClient(connectionFactory);
  }

  /**
   * @param sql
   * @param parameters
   */
  protected void clean(String sql, Object[] parameters) {
    r2dbcClient.sql(sql).bind(parameters).rowsUpdated().block();
  }

  /**
   * @param p
   * @param <T>
   * @return
   */
  protected <T> Mono<T> transactional(Mono<T> p) {
    var transactionOperator = TransactionalOperator.create(transactionManager);
    return transactionOperator
        .execute(
            status -> {
              status.setRollbackOnly();
              return p;
            })
        .last();
  }

  /**
   * @param p
   * @param <T>
   * @return
   */
  protected <T> Flux<T> transactional(Flux<T> p) {
    var transactionOperator = TransactionalOperator.create(transactionManager);
    return transactionOperator.execute(
        status -> {
          status.setRollbackOnly();
          return p;
        });
  }

  /**
   * @param sql
   * @param params
   * @return
   */
  protected Mono<Map<String, Object>> findOne(String sql, Map<String, Object> params) {
    return execute(sql, params).one();
  }

  /**
   * @param sql
   * @param params
   * @return
   */
  protected Mono<List<Map<String, Object>>> findList(String sql, Map<String, Object> params) {
    return execute(sql, params).all().collectList();
  }

  private FetchSpec<Map<String, Object>> execute(String sql, Map<String, Object> params) {
    var spec = dc.sql(sql);
    for (Entry<String, Object> entry : params.entrySet()) {
      spec = spec.bind(entry.getKey(), entry.getValue());
    }
    return spec.fetch();
  }
}
