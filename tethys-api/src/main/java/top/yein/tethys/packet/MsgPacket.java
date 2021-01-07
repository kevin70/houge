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
 * 消息 Packet.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MsgPacket extends Packet {

  /**
   * 消息 ID 全局唯一.
   *
   * @return 消息 ID
   */
  String getMsgId();

  /**
   * 消息发送者.
   *
   * @return 发送者
   */
  String getFrom();

  /**
   * 消息接收者.
   *
   * @return 接收者
   */
  String getTo();
}
