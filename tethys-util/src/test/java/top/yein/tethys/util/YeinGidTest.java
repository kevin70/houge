/*
 * Copyright 2019-2021 the original author or authors
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
package top.yein.tethys.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * {@link YeinGid} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class YeinGidTest {

  @Test
  void makeYeinGid() {
    var fid = 5432;
    var gid = new YeinGid(fid);
    var hexString1 = gid.toHexString();
    var hexString2 = gid.toHexString();
    assertThat(hexString1)
        .isEqualTo(hexString2)
        .isEqualTo(gid.toString())
        .hasSize(YeinGid.YEIN_GID_LENGTH);
  }

  @ValueSource(ints = {5432, 0, 131071})
  @ParameterizedTest
  void fromString(int fid) {
    var gid = new YeinGid(fid);
    var hexString = gid.toHexString();
    var parsedGid = YeinGid.fromString(hexString);
    assertSoftly(
        s -> {
          s.assertThat(parsedGid.getVersion()).as("version").isEqualTo(gid.getVersion());
          s.assertThat(parsedGid.getTimestamp()).as("timestamp").isEqualTo(gid.getTimestamp());
          s.assertThat(parsedGid.getSeq()).as("seq").isEqualTo(gid.getSeq());
          s.assertThat(parsedGid.getFid()).as("f2").isEqualTo(gid.getFid());
        });
  }

  @Test
  void illegalYeinGid() {
    assertThatIllegalArgumentException().isThrownBy(() -> YeinGid.fromString(null));
    assertThatIllegalArgumentException().isThrownBy(() -> YeinGid.fromString(""));
    assertThatIllegalArgumentException().isThrownBy(() -> YeinGid.fromString("4S27BZZ8ZZR*7E"));
    assertThatIllegalArgumentException().isThrownBy(() -> YeinGid.fromString("4S27BZZ#ZZR*7EMG&"));
  }

  @Test
  void illegalFid() {
    assertThatIllegalArgumentException().isThrownBy(() -> new YeinGid(-1));
    assertThatIllegalArgumentException().isThrownBy(() -> new YeinGid(131072));
  }
}
