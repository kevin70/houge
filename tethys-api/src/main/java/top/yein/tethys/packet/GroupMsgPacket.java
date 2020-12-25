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

import lombok.Value;

/**
 * 群组消息.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Value
public class GroupMsgPacket implements MsgPacket {

  /** 发送消息者. */
  String from;
  /** 接收消息者. */
  String to;
  /** 消息内容. */
  String content;
  /** 消息扩展参数. */
  String extraArgs;

  @Override
  public String getNs() {
    return Namespaces.GROUP_MSG;
  }
}
