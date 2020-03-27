package io.zhudy.xim;

/** @author Kevin Zou (kevinz@weghst.com) */
public class XimVersion {

  /**
   * 返回 xim 版本号. 如果未找到则返回 {@code dev}.
   *
   * @return xim 版本号
   */
  public static String version() {
    var v = XimVersion.class.getPackage().getImplementationVersion();
    if (v != null) {
      return v;
    }
    return "dev";
  }
}
