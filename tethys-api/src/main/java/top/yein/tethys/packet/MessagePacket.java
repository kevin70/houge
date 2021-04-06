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
package top.yein.tethys.packet;

import top.yein.tethys.constants.ContentType;

/**
 * 消息包接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessagePacket extends Packet {

  /**
   * 返回全局唯一的消息 ID.
   *
   * @return 消息 ID
   */
  String getMessageId();

  /**
   * 消息发送者 ID.
   *
   * @return 发送者 ID
   */
  Long getFrom();

  /**
   * 消息接收者.
   *
   * @return 接收者 ID
   */
  long getTo();

  /**
   * 返回消息内容.
   *
   * @return 消息内容
   */
  String getContent();

  /**
   * 返回消息内容类型.
   *
   * @return 消息内容类型
   * @see ContentType
   */
  int getContentType();

  /**
   * 返回消息扩展参数.
   *
   * @return 扩展参数
   */
  String getExtraArgs();
}
