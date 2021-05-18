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
package cool.houge.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * {@link MessageContentType} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageContentTypeTest {

  @Test
  void forCode() {
    for (MessageContentType value : MessageContentType.values()) {
      assertThat(MessageContentType.forCode(value.getCode())).isEqualTo(value);
    }
    assertThat(MessageContentType.forCode(null)).isEqualTo(MessageContentType.UNRECOGNIZED);
    assertThat(MessageContentType.forCode(9999)).isEqualTo(MessageContentType.UNRECOGNIZED);
  }
}
