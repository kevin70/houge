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
package cool.houge.logic.grpc;

import io.grpc.stub.StreamObserver;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/** @author KK (kzou227@qq.com) */
public class SingleGrpcSubscriber<T> implements Subscriber<T> {

  private final StreamObserver<T> responseObserver;

  public SingleGrpcSubscriber(StreamObserver<T> responseObserver) {
    this.responseObserver = responseObserver;
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    subscription.request(Long.MAX_VALUE);
  }

  @Override
  public void onNext(T o) {
    responseObserver.onNext(o);
    responseObserver.onCompleted();
  }

  @Override
  public void onError(Throwable t) {
    responseObserver.onError(t);
    responseObserver.onCompleted();
  }

  @Override
  public void onComplete() {}
}
