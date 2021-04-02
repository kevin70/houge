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
package top.yein.tethys.grpc.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.tethys.grpc.MessageRequest;
import top.yein.tethys.grpc.MessageResponse;
import top.yein.tethys.grpc.MessageServiceGrpc;

/**
 * gRPC 消息服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class MessageServiceGrpcImpl extends MessageServiceGrpc.MessageServiceImplBase {

  @Override
  public void send(MessageRequest request, StreamObserver<MessageResponse> responseObserver) {
    Mono.just("abc")
        .map(
            str -> {
              System.out.println("gRPC 接收到消息: " + request);
              return MessageResponse.newBuilder().setMessageId("hello id").build();
            })
        .subscribe(
            responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted);
  }
}
