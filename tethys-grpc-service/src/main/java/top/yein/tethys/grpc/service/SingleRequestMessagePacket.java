package top.yein.tethys.grpc.service;

import top.yein.tethys.grpc.MessageRequest;
import top.yein.tethys.packet.MessagePacket;
import top.yein.tethys.packet.Namespaces;

/** @author KK (kzou227@qq.com) */
class SingleRequestMessagePacket implements MessagePacket {

  private final String messageId;
  private final MessageRequest request;

  SingleRequestMessagePacket(String messageId, MessageRequest request) {
    this.messageId = messageId;
    this.request = request;
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
  public int getKind() {
    return request.getKindValue();
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

  // ==== 可优化逻辑 =====
  @Override
  public String getNs() {
    return Namespaces.NS_MESSAGE;
  }
}
