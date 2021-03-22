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
package top.yein.tethys.im.handler.internal;

import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.constants.MessageKind;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.packet.GroupMessagePacket;
import top.yein.tethys.packet.PrivateMessagePacket;

/**
 * 消息校验器.
 *
 * @author KK (kzou227@qq.com)
 */
public class MessagePacketChecker {

  public static final int MSG_ID_LENGTH = 15;

  /**
   * 私聊消息校验.
   *
   * @param packet 私聊消息
   */
  public static void check(PrivateMessagePacket packet) {
    checkMsgId(packet.getMessageId());
    // FIXME
    //    checkTo(packet.getTo());
    checkKind(packet.getContentKind());
    checkContent(packet.getContent());
  }

  /**
   * 群组消息校验.
   *
   * @param packet 群组消息
   */
  public static void check(GroupMessagePacket packet) {
    checkMsgId(packet.getMessageId());
    checkTo(packet.getTo());
    checkKind(packet.getContentKind());
    checkContent(packet.getContent());
  }

  static void checkMsgId(String msgId) {
    if (msgId == null || msgId.length() != MSG_ID_LENGTH) {
      throw new BizCodeException(BizCodes.C3600, "[msg_id]不能为空且必须是一个长度为14的字符串");
    }
  }

  static void checkTo(Long to) {
    if (to == null) {
      throw new BizCodeException(BizCodes.C3600, "[to]不能为空");
    }
  }

  static void checkKind(int kind) {
    try {
      MessageKind.forCode(kind);
    } catch (IllegalArgumentException e) {
      throw new BizCodeException(BizCodes.C3600, "[kind]值不合法");
    }
  }

  static void checkContent(String content) {
    if (content == null || content.isEmpty()) {
      throw new BizCodeException(BizCodes.C3600, "[content]值能为空");
    }
  }
}
