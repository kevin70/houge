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
package top.yein.tethys.service;

import com.google.common.base.Strings;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.tethys.core.util.MonoSinkStreamObserver;
import top.yein.tethys.grpc.MessageGrpc;
import top.yein.tethys.grpc.MessageRequest;
import top.yein.tethys.grpc.MessageResponse;
import top.yein.tethys.service.result.MessageSendResult;
import top.yein.tethys.vo.MessageSendVo;

/**
 * 远程消息服务调用逻辑实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class RemoteMessageServiceImpl implements RemoteMessageService {

  private final MessageGrpc.MessageStub messageStub;

  /**
   * 可被 IoC 容器管理的构造函数.
   *
   * @param messageStub gRPC 消息服务存根
   */
  @Inject
  public RemoteMessageServiceImpl(MessageGrpc.MessageStub messageStub) {
    this.messageStub = messageStub;
  }

  @Override
  public Mono<MessageSendResult> sendMessage(long senderId, MessageSendVo vo) {
    var builder =
        MessageRequest.newBuilder()
            .setKindValue(vo.getKind())
            .setFrom(senderId)
            .setTo(vo.getTo())
            .setContent(vo.getContent())
            .setContentTypeValue(vo.getContentType());
    if (!Strings.isNullOrEmpty(vo.getExtraArgs())) {
      builder.setExtraArgs(vo.getExtraArgs());
    }

    return Mono.<MessageResponse>create(
            sink -> {
              log.debug("发送gRPC消息 vo={}", vo);
              messageStub.send(builder.build(), new MonoSinkStreamObserver<>(sink));
            })
        .map(
            response -> {
              if (log.isDebugEnabled()) {
                log.debug("收到gRPC消息回应 messageId={} vo={}", response.getMessageId(), vo);
              }
              return MessageSendResult.builder().messageId(response.getMessageId()).build();
            });
  }
}
