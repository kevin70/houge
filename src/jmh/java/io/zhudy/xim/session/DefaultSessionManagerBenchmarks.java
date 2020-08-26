/*
 * Copyright 2019-2020 the original author or authors
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
package io.zhudy.xim.session;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.session.impl.DefaultSessionManager;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/** @author Kevin Zou (kevinz@weghst.com) */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class DefaultSessionManagerBenchmarks {

  private SessionManager sessionManager;
  private Random sessionIdRan = new SecureRandom();

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
    public Mono<Void> send(Publisher<TextWebSocketFrame> frame) {
      return null;
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
