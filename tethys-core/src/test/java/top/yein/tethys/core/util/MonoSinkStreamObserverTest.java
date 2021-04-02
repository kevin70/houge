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
package top.yein.tethys.core.util;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * {@link MonoSinkStreamObserver} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MonoSinkStreamObserverTest {

  @Test
  void onNext() {
    var v = "hello";
    var p =
        Mono.<String>create(
            sink -> {
              var observer = new MonoSinkStreamObserver<>(sink);
              observer.onNext(v);
            });
    StepVerifier.create(p).expectNext(v).expectComplete().verify();
  }

  @Test
  void onError() {
    var ex = new IllegalArgumentException();
    var p =
        Mono.<String>create(
            sink -> {
              var observer = new MonoSinkStreamObserver<>(sink);
              observer.onError(ex);
            });
    StepVerifier.create(p).expectErrorMatches(t -> t == ex).verify();
  }
}
