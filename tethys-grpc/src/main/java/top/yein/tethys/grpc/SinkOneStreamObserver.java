package top.yein.tethys.grpc;

import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * {@link StreamObserver} {@link Sinks.One}的适配器.
 *
 * @author KK (kzou227@qq.com)
 */
public class SinkOneStreamObserver<Resp> implements StreamObserver<Resp> {

  private final Sinks.One<Resp> sink = Sinks.one();

  @Override
  public void onNext(Resp value) {
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

  /** @return */
  public Mono<Resp> asMono() {
    return sink.asMono();
  }
}
