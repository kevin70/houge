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

import top.yein.tethys.grpc.MessageRequest;
import top.yein.tethys.packet.MessagePacket;
import top.yein.tethys.packet.Namespaces;

/**
 * gRPC 单消息发送请求.
 *
 * @author KK (kzou227@qq.com)
 */
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
