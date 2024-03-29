/*
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
package cool.houge;

/**
 * 应用的版本号.
 *
 * @author KK (kzou227@qq.com)
 */
public final class Version {

  private Version() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 返回应用版本号.
   *
   * <p>如果未找到则返回 {@code unknown}.
   *
   * @return 版本号
   */
  public static String version() {
    var v = Version.class.getPackage().getImplementationVersion();
    if (v != null) {
      return v;
    }
    return "unknown";
  }
}
