/*
 * Copyright 2019-2020 the original author or authors
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
package top.yein.tethys.packet;

import static top.yein.tethys.packet.Namespaces.NS_MESSAGE;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import top.yein.tethys.constants.MessageContentType;

/**
 * 通用的消息包.
 *
 * @author KK (kzou227@qq.com)
 */
@JsonTypeName(NS_MESSAGE)
@Data
public class SimpleMessagePacket implements MessagePacket {

  /** 消息 ID 全局唯一. */
  String messageId;
  /** 发送消息者. */
  Long from;
  /** 接收消息者. */
  long to;
  /** 消息内容. */
  String content;
  /** 消息类型 {@link MessageContentType}. */
  int contentType;
  /** 扩展参数. */
  String extraArgs;

  @Override
  public String getMessageId() {
    return messageId;
  }

  @Override
  public Long getFrom() {
    return from;
  }

  @Override
  public long getTo() {
    return to;
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public int getContentType() {
    return contentType;
  }

  @Override
  public String getExtraArgs() {
    return extraArgs;
  }

  @Override
  public String getNs() {
    return NS_MESSAGE;
  }
}
