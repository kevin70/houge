/**
 * Copyright 2019-2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
