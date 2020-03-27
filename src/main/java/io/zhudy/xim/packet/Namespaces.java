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
  public static final String GROUP_SUBSCRIBE = "group.subscribe";
  /**
   * 取消订阅群组消息.
   *
   * @see GroupUnsubPacket
   */
  public static final String GROUP_UNSUBSCRIBE = "group.unsubscribe";
}
