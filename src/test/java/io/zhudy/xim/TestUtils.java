package io.zhudy.xim;

/** @author Kevin Zou (kevinz@weghst.com) */
public class TestUtils {

  /**
   *
   * @param e
   * @param expect
   * @return
   */
  public static boolean test(Throwable e, BizCode expect) {
    BizCodeException a = (BizCodeException) e;
    return a.getBizCode() == expect;
  }
}
