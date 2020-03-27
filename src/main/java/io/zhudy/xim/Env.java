package io.zhudy.xim;

import java.util.Arrays;

/**
 * xim 的运行环境枚举定义.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public enum Env {

  /** 测试环境. */
  TEST,
  /** 集成测试环境. */
  INTEGRATION_TEST,
  /** 生产环境. */
  PROD,
  ;

  /** 当前的应用环境. */
  private final static Env C = getEnv();

  /**
   * 当前运行的环境. 默认为 {@link #PROD}.
   *
   * <p>可通过下列方式设置 xim 的运行环境. <b>按序号查找，忽略大小写</b>.
   *
   * <ul>
   *   <li>>通过系统环境变量 {@code XIM_ENV} 设置
   *   <li>通过 java 启动时命令行参数 {@code -Dxim.env=test} 设置
   * </ul>
   */
  public static Env current() {
    return C;
  }

  static Env getEnv() {
    var k = "XIM_ENV";
    var v = System.getenv(k);
    if (v == null) {
      k = "xim.env";
      v = System.getProperty(k);
    }
    if (v == null) {
      return PROD;
    }
    for (Env value : values()) {
      if (value.name().equalsIgnoreCase(v)) {
        return value;
      }
    }
    throw new IllegalArgumentException(
        "非法的 \"" + k + "\" 值 \"" + v + "\"，可选值为 " + Arrays.stream(values()));
  }
}
