package top.yein.tethys.storage.tx;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.log4j.Log4j2;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * 事务拦截器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class ReactorTransactionalInterceptor implements MethodInterceptor {

  private final ConnectionFactory connectionFactory;

  public ReactorTransactionalInterceptor(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    var rs = invocation.proceed();
    if (rs instanceof Mono) {
      return Mono.from((Publisher<?>) rs).contextWrite(newContext());
    }
    if (rs instanceof Flux) {
      return Flux.from((Publisher<?>) rs).contextWrite(newContext());
    }
    log.warn("非响应式返回无法代理");
    return rs;
  }

  private Context newContext() {
    return Context.of(ConnectionHolder.class, null);
  }
}
