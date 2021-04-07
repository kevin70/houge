package top.yein.tethys.constants;

import top.yein.tethys.EnumLite;

/**
 * 消息类型枚举.
 *
 * <p>{@link #UNRECOGNIZED} 是枚举的空值.
 *
 * @author KK (kzou227@qq.com)
 */
public enum MessageKind implements EnumLite {

  /** 不认识未被承认的枚举. */
  UNRECOGNIZED(-1),
  /** 私聊消息. */
  P_MESSAGE(0),
  /** 群组消息. */
  G_MESSAGE(1),
  /** 系统消息. */
  S_MESSAGE(9),
  ;

  private final int code;

  MessageKind(int code) {
    this.code = code;
  }

  @Override
  public int getCode() {
    return this.code;
  }

  /**
   * 将给定数值转换为枚举.
   *
   * <p>如果给定的数值未查找到对应的枚举则会返回 {@link #UNRECOGNIZED}.
   *
   * @param code 对应枚举项的数值
   * @return 与给定数值关联的枚举
   */
  public static MessageKind forCode(Integer code) {
    if (code == null) {
      return UNRECOGNIZED;
    }
    if (code == P_MESSAGE.code) {
      return P_MESSAGE;
    }
    if (code == G_MESSAGE.code) {
      return G_MESSAGE;
    }
    if (code == S_MESSAGE.code) {
      return S_MESSAGE;
    }
    return UNRECOGNIZED;
  }
}
