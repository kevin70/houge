package io.zhudy.xim;

/**
 * 业务错误码定义.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public enum BizCodes implements BizCode {
  C0("未知错误"),
  C401("认证失败"),
  C404("未找到"),

  // ---------------------------------------------------------------------//
  // 3300 - 3350 >> 会话 TOKEN 相关的错误码
  // ---------------------------------------------------------------------//
  C3300("非法的令牌"),
  C3301("已过期的令牌"),
  C3302("未成熟的令牌"),
  C3305("JWT 参数错误"),

  // ---------------------------------------------------------------------//
  // 3500 - 3550 >> 会话 SESSION 相关的错误码
  // ---------------------------------------------------------------------//
  C3500("sessionId 冲突"),
  ;
  private final int code;
  private final String message;

  BizCodes(String message) {
    this.code = Integer.parseInt(this.name().substring(1));
    this.message = message;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
