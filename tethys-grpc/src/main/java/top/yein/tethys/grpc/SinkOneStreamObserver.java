package top.yein.tethys.grpc;

import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * {@link StreamObserver} {@link Sinks.One}的适配器.
 *
 * @author KK (kzou227@qq.com)
 */
public class SinkOneStreamObserver<RESP> implements StreamObserver<RESP> {

  private final Sinks.One<RESP> sink = Sinks.one();

  @Override
  public void onNext(RESP value) {
    sink.tryEmitValue(value).orThrow();
  }

  @Override
  public void onError(Throwable t) {
    sink.tryEmitError(t).orThrow();
  }

  @Override
  public void onCompleted() {
    // ignore
  }

  /**
   * 返回观察者的 Mono 实例每次调用都返回相同的实例.
   *
   * @return 观察者的 Mono 实例
   */
  public Mono<RESP> asMono() {
    return sink.asMono();
  }
}
