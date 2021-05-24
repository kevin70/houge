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

import cool.houge.grpc.MessageGrpc;
import cool.houge.grpc.MessagePb;
import cool.houge.grpc.MessagePb.SendMessageRequest;
import cool.houge.grpc.MessagePb.SendMessageResponse;
import cool.houge.id.MessageIdGenerator;
import cool.houge.logic.handler.GroupMessageHandler;
import cool.houge.logic.handler.PrivateMessageHandler;
import cool.houge.logic.packet.MessagePacketBase;
import cool.houge.logic.packet.Packet;
import io.grpc.stub.StreamObserver;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 发送消息 gRPC 实现类.
 *
 * @author KK (kzou227@qq.com)
 */
public class MessageGrpcImpl extends MessageGrpc.MessageImplBase {

  private static final Logger log = LogManager.getLogger();

  private final MessageIdGenerator messageIdGenerator;
  private final PrivateMessageHandler privateMessageHandler;
  private final GroupMessageHandler groupMessageHandler;

  /**
   * 构造函数.
   *
   * @param messageIdGenerator 消息ID生成器
   * @param privateMessageHandler 私人消息处理器
   * @param groupMessageHandler 群组消息处理器
   */
  @Inject
  public MessageGrpcImpl(
      MessageIdGenerator messageIdGenerator,
      PrivateMessageHandler privateMessageHandler,
      GroupMessageHandler groupMessageHandler) {
    this.messageIdGenerator = messageIdGenerator;
    this.privateMessageHandler = privateMessageHandler;
    this.groupMessageHandler = groupMessageHandler;
  }

  @Override
  public void sendToUser(
      MessagePb.SendMessageRequest request,
      StreamObserver<MessagePb.SendMessageResponse> responseObserver) {
    var packet = new GrpcMessagePacket(Packet.NS_PRIVATE_MESSAGE, request);
    privateMessageHandler
        .handle(packet)
        .map(
            rs -> {
              var response =
                  MessagePb.SendMessageResponse.newBuilder().setMessageId(packet.messageId).build();
              return response;
            })
        .subscribe(new SingleGrpcSubscriber<>(responseObserver));
  }

  @Override
  public void sendToGroup(
      SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
    var packet = new GrpcMessagePacket(Packet.NS_GROUP_MESSAGE, request);
    groupMessageHandler
        .handle(packet)
        .map(
            rs -> {
              var response =
                  MessagePb.SendMessageResponse.newBuilder().setMessageId(packet.messageId).build();
              return response;
            })
        .subscribe(new SingleGrpcSubscriber<>(responseObserver));
  }

  private class GrpcMessagePacket extends MessagePacketBase {
    private final String ns;
    private final MessagePb.SendMessageRequest request;
    private final String messageId;

    private GrpcMessagePacket(String ns, SendMessageRequest request) {
      this.ns = ns;
      this.messageId = messageIdGenerator.nextId();
      this.request = request;
    }

    @Override
    public String getNs() {
      return ns;
    }

    @Override
    public String getMessageId() {
      return messageId;
    }

    @Override
    public Long getFrom() {
      return request.getFrom();
    }

    @Override
    public long getTo() {
      return request.getTo();
    }

    @Override
    public String getContent() {
      return request.getContent();
    }

    @Override
    public int getContentType() {
      return request.getContentTypeValue();
    }

    @Override
    public String getExtraArgs() {
      return request.getExtraArgs();
    }
  }
}
