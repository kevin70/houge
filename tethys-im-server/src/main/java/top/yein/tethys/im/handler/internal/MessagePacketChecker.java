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

  public static final int MSG_ID_LENGTH = 14;

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
    checkMsgId(packet.getMsgId());
    checkTo(packet.getTo());
    checkKind(packet.getKind());
    checkContent(packet.getContent());
  }

  static void checkMsgId(String msgId) {
    if (msgId == null || msgId.length() != MSG_ID_LENGTH) {
      throw new BizCodeException(BizCodes.C3600, "[msg_id]不能为空且必须是一个长度为14的字符串");
    }
  }

  static void checkTo(String to) {
    if (to == null || to.isEmpty()) {
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
