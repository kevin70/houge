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

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.yein.tethys.grpc.MessageGrpc;
import top.yein.tethys.grpc.MessageRequest;
import top.yein.tethys.grpc.MessageResponse;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.message.MessageRouter;
import top.yein.tethys.service.MessageStorageService;

/**
 * gRPC 消息服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public final class MessageGrpcImpl extends MessageGrpc.MessageImplBase {

  private final MessageIdGenerator messageIdGenerator;
  private final MessageStorageService messageStorageService;
  private final MessageRouter messageRouter;

  /**
   * 可被 IoC 容器使用的构造函数.
   *
   * @param messageIdGenerator 消息 ID 生成器
   * @param messageStorageService 消息存储服务
   * @param messageRouter 消息路由器
   */
  @Inject
  public MessageGrpcImpl(
      MessageIdGenerator messageIdGenerator,
      MessageStorageService messageStorageService,
      MessageRouter messageRouter) {
    this.messageIdGenerator = messageIdGenerator;
    this.messageStorageService = messageStorageService;
    this.messageRouter = messageRouter;
  }

  @Override
  public void send(MessageRequest request, StreamObserver<MessageResponse> observer) {
    Mono.defer(() -> send0(request))
        .doOnError(ex -> log.error("处理 gRPC 发送的消息异常 {}", request, ex))
        .subscribeOn(Schedulers.parallel())
        .subscribe(observer::onNext, observer::onError, observer::onCompleted);
  }

  private Mono<MessageResponse> send0(MessageRequest request) {
    log.debug("接收消息请求 {}", request);
    // 消息校验
    if (request.getKind() == MessageRequest.Kind.UNRECOGNIZED) {
      throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("[kind]非法的消息类型"));
    }
    if (request.getFrom() <= 0) {
      throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("[from]必须是一个正整数ID"));
    }
    if (request.getTo() <= 0) {
      throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("[to]必须是一个正整数ID"));
    }
    if (request.getContentType() == MessageRequest.ContentType.UNRECOGNIZED) {
      throw new StatusRuntimeException(
          Status.INVALID_ARGUMENT.withDescription("[content_type]非法的消息内容类型"));
    }

    var messageId = messageIdGenerator.nextId();
    var messagePacket = new SingleRequestMessagePacket(messageId, request);
    return Mono.when(
            messageStorageService.store(messagePacket),
            messageRouter.route(messagePacket, inputSession -> true))
        .thenReturn(MessageResponse.newBuilder().setMessageId(messageId).build());
  }
}
