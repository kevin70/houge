package top.yein.tethys.repository;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public abstract class AbstractTestRepository {

  @Autowired protected DatabaseClient dc;
  @Autowired protected ReactiveTransactionManager transactionManager;

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
        .next();
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
