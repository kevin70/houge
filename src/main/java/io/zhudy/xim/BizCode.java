package io.zhudy.xim;

/**
 * 业务错误码接口.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface BizCode {

  /**
   * 业务错误码.
   *
   * @return 错误码
   */
  int getCode();

  /**
   * 业务错误码描述.
   *
   * @return 错误码描述
   */
  String getMessage();
}
