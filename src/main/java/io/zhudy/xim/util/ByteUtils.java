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
package io.zhudy.xim.util;

/**
 * Byte 工具类.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public final class ByteUtils {

  private ByteUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 测试 {@code haystack} 是否以给定的前缀开头.
   *
   * @param haystack 要扫描的源
   * @param prefix 要查找的前缀
   * @return 如果返回 {@code true} 表示源数据是以前缀开头
   * @see #startsWith(byte[], byte[], int)
   */
  public static boolean startsWith(byte[] haystack, byte[] prefix) {
    return startsWith(haystack, prefix, 0);
  }

  /**
   * 测试从指定的偏移量开始的 {@code haystack} 是否以给定的前缀开头.
   *
   * @param haystack 要扫描的源
   * @param prefix 要查找的前缀
   * @param offset 起始偏移量
   * @return 如果返回 {@code true} 表示源数据是以前缀开头
   */
  public static boolean startsWith(byte[] haystack, byte[] prefix, int offset) {
    int to = offset;
    int prefixOffset = 0;
    int prefixLength = prefix.length;

    if ((offset < 0) || (offset > haystack.length - prefixLength)) {
      return false;
    }

    while (--prefixLength >= 0) {
      if (haystack[to++] != prefix[prefixOffset++]) {
        return false;
      }
    }

    return true;
  }
}
