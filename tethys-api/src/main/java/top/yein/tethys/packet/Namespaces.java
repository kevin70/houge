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

/**
 * 消息包的命名空间常量定义与 {@link Packet#getNs()} 属性对应.
 *
 * @author KK (kzou227@qq.com)
 */
public final class Namespaces {

  private Namespaces() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 错误消息.
   *
   * @see ErrorPacket
   */
  public static final String NS_ERROR = "error";
  /**
   * 心跳消息.
   *
   * <p>浏览器对于 WebSocket 的 ping/pong 消息支持各不相同，单独提供一个 ping 协议消息。
   *
   * <p>由客户端发起确保连接的可用性。
   */
  public static final String NS_PING = "ping";
  /**
   * 心路响应消息.
   *
   * <p>与 {@link #NS_PING} 消息对应, 当接收消息客户端发送的 {@link #NS_PING} 消息后响应 {@link #NS_PONG}.
   */
  public static final String NS_PONG = "pong";
  /**
   * 消息.
   */
  public static final String NS_MESSAGE = "message";
  /**
   * 私人聊天消息.
   *
   * @see PrivateMessagePacket
   */
  public static final String NS_PRIVATE_MESSAGE = "p.message";
  /**
   * 群组聊天消息.
   *
   * @see GroupMessagePacket
   */
  public static final String NS_GROUP_MESSAGE = "g.message";

  // ================================== 操作类消息 ==================================//

  /**
   * 订阅群组消息.
   *
   * @see GroupSubscribePacket
   */
  public static final String NS_GROUP_SUBSCRIBE = "g.subscribe";
  /**
   * 取消订阅群组消息.
   *
   * @see GroupUnsubscribePacket
   */
  public static final String NS_GROUP_UNSUBSCRIBE = "g.unsubscribe";

  // ================================== 操作类消息 ==================================//
}
