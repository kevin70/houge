package io.zhudy.xim.session;

import io.netty.buffer.ByteBuf;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.session.impl.DefaultSessionManager;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/** @author Kevin Zou (kevinz@weghst.com) */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class DefaultSessionManagerBenchmarks {

  private SessionManager sessionManager;
  private ThreadLocalRandom sessionIdRan = ThreadLocalRandom.current();

  private class BenchSession implements Session {

    private final long sessionId = sessionIdRan.nextLong();

    @Override
    public long sessionId() {
      return sessionId;
    }

    @Override
    public String uid() {
      return Long.toHexString(sessionId);
    }

    @Override
    public AuthContext authContext() {
      return AuthContext.NONE_AUTH_CONTEXT;
    }

    @Override
    public Mono<Void> send(ByteBuf buf) {
      return Mono.empty();
    }

    @Override
    public Mono<Void> close() {
      return Mono.empty();
    }

    @Override
    public Mono<Void> onClose() {
      return Mono.empty();
    }
  }

  @Setup
  public void setup() {
    sessionManager = new DefaultSessionManager();
  }

  @Benchmark
  public void measureAdd$Remove() {
    var session = new BenchSession();
    sessionManager.add(session).then(sessionManager.remove(session)).block();
  }

  public static void main(String[] args) throws RunnerException {
    Options opts =
        new OptionsBuilder()
            .include(DefaultSessionManagerBenchmarks.class.getSimpleName())
            .warmupIterations(5)
            .measurementIterations(5)
            .threads(1)
            .forks(1)
            .build();
    new Runner(opts).run();
  }
}
