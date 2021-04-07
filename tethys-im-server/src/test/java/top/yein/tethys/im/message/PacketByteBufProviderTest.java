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
package top.yein.tethys.im.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import top.yein.tethys.packet.PingPacket;

/**
 * {@link PacketByteBufProvider} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class PacketByteBufProviderTest {

  @Test
  void retainedByteBuf() {
    var packet = new PingPacket();
    var provider = new PacketByteBufProvider(packet);
    var byteBuf1 = provider.retainedByteBuf();
    assertThat(byteBuf1.refCnt()).isOne();
    assertThat(provider.obtainByteBuf().refCnt()).isEqualTo(2);

    var byteBuf2 = provider.retainedByteBuf();
    assertThat(byteBuf2.refCnt()).isOne();
    assertThat(provider.obtainByteBuf().refCnt()).isEqualTo(3);

    assertThat(byteBuf1.release()).isTrue();
    assertThat(byteBuf2.release()).isTrue();
    assertThat(provider.obtainByteBuf().release()).isTrue();
  }

  @Test
  void obtainByteBufNull() {
    var packet = new PingPacket();
    var provider = new PacketByteBufProvider(packet);
    assertThat(provider.obtainByteBuf()).isNull();
  }
}
