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
package top.yein.tethys.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * {@link MessageReadStatus} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageReadStatusTest {

  @Test
  void forCode() {
    for (MessageReadStatus value : MessageReadStatus.values()) {
      assertThat(MessageReadStatus.forCode(value.getCode())).isEqualTo(value);
    }
    assertThat(MessageReadStatus.forCode(null)).isEqualTo(MessageReadStatus.UNRECOGNIZED);
    assertThat(MessageReadStatus.forCode(9999)).isEqualTo(MessageReadStatus.UNRECOGNIZED);
  }
}
