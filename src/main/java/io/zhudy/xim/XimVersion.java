package io.zhudy.xim;

/**
 * 当前 XIM 应用的版本号.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public final class XimVersion {

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
