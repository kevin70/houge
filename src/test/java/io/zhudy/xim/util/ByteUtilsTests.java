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

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ByteUtils} 单元测试.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
class ByteUtilsTests {

  @Test
  void startsWith() {
    var haystack = "p:hello".getBytes(StandardCharsets.UTF_8);
    var prefix = "p:".getBytes(StandardCharsets.UTF_8);
    assertThat(ByteUtils.startsWith(haystack, prefix)).isTrue();
    assertThat(ByteUtils.startsWith(haystack, "none".getBytes(StandardCharsets.UTF_8))).isFalse();
  }

  @Test
  void startsWith2() {
    var haystack = "-p:hello".getBytes(StandardCharsets.UTF_8);
    var prefix = "p:".getBytes(StandardCharsets.UTF_8);
    assertThat(ByteUtils.startsWith(haystack, prefix, 1)).isTrue();
    assertThat(ByteUtils.startsWith(haystack, prefix, 0)).isFalse();
  }
}
