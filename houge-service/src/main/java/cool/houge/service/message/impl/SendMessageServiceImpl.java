package cool.houge.service.message.impl;

import cool.houge.grpc.MessageGrpc.MessageStub;
import cool.houge.grpc.MessagePb.SendMessageRequest;
import cool.houge.service.message.SendMessageInput;
import cool.houge.service.message.SendMessageService;
import reactor.core.publisher.Mono;

/** @author KK (kzou227@qq.com) */
public class SendMessageServiceImpl implements SendMessageService {

  private final MessageStub messageStub;

  public SendMessageServiceImpl(MessageStub messageStub) {
    this.messageStub = messageStub;
  }

  @Override
  public Mono<Void> sendToUser(SendMessageInput input) {
    var request = SendMessageRequest.newBuilder();
    return null;
  }

  @Override
  public Mono<Void> sendToGroup(SendMessageInput input) {
    return null;
  }
}
