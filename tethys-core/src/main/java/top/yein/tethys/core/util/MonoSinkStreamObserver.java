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

import io.grpc.stub.StreamObserver;
import java.util.Objects;
import reactor.core.publisher.MonoSink;

/**
 * 适配 {@link MonoSink} 的 gRPC 响应观察者.
 *
 * @author KK (kzou227@qq.com)
 */
public class MonoSinkStreamObserver<T> implements StreamObserver<T> {

  private final MonoSink<T> sink;

  /**
   * 使用 {@link MonoSink} 创建对象.
   *
   * @param sink {@code MonoSink}
   */
  public MonoSinkStreamObserver(MonoSink<T> sink) {
    Objects.requireNonNull(sink, "[sink] 不能为空");
    this.sink = sink;
  }

  @Override
  public void onNext(T value) {
    sink.success(value);
  }

  @Override
  public void onError(Throwable t) {
    sink.error(t);
  }

  @Override
  public void onCompleted() {
    // nothing
  }
}
