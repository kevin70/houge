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

import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.core.util.MonoSinkStreamObserver;
import top.yein.tethys.grpc.MessageRequest;
import top.yein.tethys.grpc.MessageRequest.ContentKind;
import top.yein.tethys.grpc.MessageRequest.Namespace;
import top.yein.tethys.grpc.MessageResponse;
import top.yein.tethys.grpc.MessageServiceGrpc;
import top.yein.tethys.grpc.MessageServiceGrpc.MessageServiceStub;
import top.yein.tethys.packet.Namespaces;
import top.yein.tethys.service.result.MessageSendResult;
import top.yein.tethys.vo.MessageSendVo;

/**
 * 远程消息服务调用逻辑实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class RemoteMessageServiceImpl implements RemoteMessageService {

  private final MessageServiceGrpc.MessageServiceStub messageServiceStub;

  /**
   * 可被 IoC 容器管理的构造函数.
   *
   * @param messageServiceStub gRPC 消息服务存根
   */
  @Inject
  public RemoteMessageServiceImpl(MessageServiceStub messageServiceStub) {
    this.messageServiceStub = messageServiceStub;
  }

  @Override
  public Mono<MessageSendResult> sendMessage(long senderId, MessageSendVo vo) {
    MessageRequest.Namespace ns;
    if (Namespaces.NS_PRIVATE_MESSAGE.equals(vo.getNs())) {
      ns = Namespace.P_MESSAGE;
    } else if (Namespaces.NS_GROUP_MESSAGE.equals(vo.getNs())) {
      ns = Namespace.G_MESSAGE;
    } else {
      throw new BizCodeException(BizCode.C400, "不支持的 ns").addContextValue("ns", vo.getNs());
    }

    var contentKind = MessageRequest.ContentKind.forNumber(vo.getContentKind());
    if (contentKind == null || contentKind == ContentKind.UNRECOGNIZED) {
      throw new BizCodeException(BizCode.C400, "不支持的 content_kind")
          .addContextValue("content_kind", vo.getContentKind());
    }

    var builder =
        MessageRequest.newBuilder()
            .setNs(ns)
            .setFrom(senderId)
            .addTo(vo.getTo())
            .setContent(vo.getContent())
            .setContentKind(contentKind);
    if (vo.getUrl() != null) {
      builder.setUrl(vo.getUrl());
    }
    if (vo.getCustomArgs() != null) {
      builder.setCustomArgs(vo.getCustomArgs());
    }

    return Mono.<MessageResponse>create(
            sink -> {
              log.debug("调用远程接口");
              messageServiceStub.send(builder.build(), new MonoSinkStreamObserver<>(sink));
            })
        .map(response -> MessageSendResult.builder().messageId(response.getMessageId()).build());
  }
}
