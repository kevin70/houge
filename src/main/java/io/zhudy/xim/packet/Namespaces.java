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
package io.zhudy.xim.packet;

/**
 * 消息包的命名空间常量定义与 {@link Packet#getNs()} 属性对应.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public final class Namespaces {

  /**
   * 错误消息.
   *
   * @see ErrorPacket
   */
  public static final String ERROR = "error";
  /**
   * 私人聊天消息.
   *
   * @see PrivateMsgPacket
   */
  public static final String PRIVATE_MSG = "private.msg";
  /**
   * 群组聊天消息.
   *
   * @see GroupMsgPacket
   */
  public static final String GROUP_MSG = "group.msg";
  /**
   * 订阅群组消息.
   *
   * @see GroupSubPacket
   */
  public static final String GROUP_SUBSCRIBE = "group.sub";
  /**
   * 取消订阅群组消息.
   *
   * @see GroupUnsubPacket
   */
  public static final String GROUP_UNSUBSCRIBE = "group.unsub";
}
