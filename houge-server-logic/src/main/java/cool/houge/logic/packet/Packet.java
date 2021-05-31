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
package cool.houge.logic.packet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * 消息包接口.
 *
 * @author KK (kzou227@qq.com)
 */
@JsonTypeInfo(
    use = Id.NAME,
    property = Packet.NS_JSON_PROPERTY_NAME,
    include = As.EXISTING_PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(name = Packet.NS_PRIVATE_MESSAGE, value = PrivateMessagePacket.class),
  @JsonSubTypes.Type(name = Packet.NS_GROUP_MESSAGE, value = GroupMessagePacket.class),
})
@JsonPropertyOrder(Packet.NS_JSON_PROPERTY_NAME)
public interface Packet {

  /** {@code @ns} JSON 属性名称. */
  String NS_JSON_PROPERTY_NAME = "@ns";

  /** 错误命名空间. */
  String NS_ERROR = "error";
  /** 私人聊天消息命名空间. */
  String NS_PRIVATE_MESSAGE = "p.message";
  /** 群组聊天消息命名空间. */
  String NS_GROUP_MESSAGE = "g.message";
  /** 系统消息命名空间. */
  String NS_SYSTEM_MESSAGE = "s.message";

  /**
   * 返回命名空间.
   *
   * @return 命名空间
   */
  @JsonProperty(NS_JSON_PROPERTY_NAME)
  String getNs();
}
