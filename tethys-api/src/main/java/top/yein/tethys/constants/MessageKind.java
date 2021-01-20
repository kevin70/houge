package top.yein.tethys.constants;

/**
 * 消息类型枚举定义.
 *
 * @author KK (kzou227@qq.com)
 */
public enum MessageKind {
  /** 普通文本消息. */
  TEXT(1),
  /** 图片消息. */
  IMAGE(2),
  /** 语音消息. */
  VOICE(3),
  /** 视频消息. */
  VIDEO(4),
  ;
  private final int code;

  MessageKind(int code) {
    this.code = code;
  }

  /**
   * 消息类型代码.
   *
   * @return 代码
   */
  public int getCode() {
    return code;
  }

  /**
   * 将类型代码转换为枚举.
   *
   * @param code 代码
   * @return 枚举
   * @throws IllegalArgumentException 非法的消息码
   */
  public static MessageKind forCode(int code) throws IllegalArgumentException {
    for (MessageKind v : values()) {
      if (v.code == code) {
        return v;
      }
    }
    throw new IllegalArgumentException("非法的 MessageKind 代码 \"" + code + "\"");
  }
}
