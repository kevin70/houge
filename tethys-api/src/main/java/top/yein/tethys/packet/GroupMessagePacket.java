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

import static top.yein.tethys.packet.Namespaces.NS_GROUP_MESSAGE;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Value;
import top.yein.tethys.constants.MessageKind;

/**
 * 群组消息.
 *
 * @author KK (kzou227@qq.com)
 */
@JsonTypeName(NS_GROUP_MESSAGE)
@Value
public class GroupMessagePacket implements MsgPacket {

  /** 发送消息者. */
  @Getter(onMethod_ = {@Override})
  String from;
  /** 接收消息者. */
  @Getter(onMethod_ = {@Override})
  String to;
  /** 消息类型 {@link top.yein.tethys.constants.MessageKind}. */
  int kind = MessageKind.TEXT.getCode();
  /** 消息内容. */
  String content;
  /** 统一资源定位器, 图片URL, 视频URL. */
  String url;
  /** 自定义消息参数. */
  String customArgs;

  @Override
  public String getNs() {
    return NS_GROUP_MESSAGE;
  }
}
