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

import com.google.common.base.CharMatcher;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.constants.ContentType;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.packet.GroupMessagePacket;
import top.yein.tethys.packet.PrivateMessagePacket;
import top.yein.tethys.util.YeinGid;

/**
 * 消息包校验器.
 *
 * @author KK (kzou227@qq.com)
 */
public class MessagePacketChecker {

  private static final int MESSAGE_ID_LENGTH = YeinGid.YEIN_GID_LENGTH;

  private MessagePacketChecker() {}

  /**
   * 私聊消息包校验.
   *
   * @param packet 私聊包消息
   */
  public static void check(PrivateMessagePacket packet) {
    checkMessageId(packet.getMessageId());
    checkTo(packet.getTo());
    checkContentKind(packet.getContentKind());
    checkContent(packet.getContent());
  }

  /**
   * 群组消息包校验.
   *
   * @param packet 群组包消息
   */
  public static void check(GroupMessagePacket packet) {
    checkMessageId(packet.getMessageId());
    checkTo(packet.getTo());
    checkContent(packet.getContent());
    checkContentKind(packet.getContentKind());
  }

  static void checkMessageId(String messageId) {
    if (messageId == null || messageId.length() != MESSAGE_ID_LENGTH) {
      throw new BizCodeException(BizCodes.C3600, "[message_id]不能为空且必须是一个长度为 15 的字符串");
    }
    if (CharMatcher.whitespace().matchesAnyOf(messageId)) {
      throw new BizCodeException(BizCodes.C3600, "[message_id]不能包含空白字符");
    }
  }

  static void checkTo(Long to) {
    if (to == null) {
      throw new BizCodeException(BizCodes.C3600, "[to]不能为空");
    }
  }

  static void checkContent(String content) {
    if (content == null || content.isEmpty()) {
      throw new BizCodeException(BizCodes.C3600, "[content]值能为空");
    }
  }

  static void checkContentKind(int kind) {
    if (ContentType.forCode(kind) == ContentType.UNRECOGNIZED) {
      throw new BizCodeException(BizCodes.C3600, "[kind]值不合法");
    }
  }
}
