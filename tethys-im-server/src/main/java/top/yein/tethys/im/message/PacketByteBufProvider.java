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

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.util.PacketUtils;
import top.yein.tethys.packet.Packet;

/** @author KK (kzou227@qq.com) */
class PacketByteBufProvider {

  private final Packet packet;
  private AtomicReference<ByteBuf> byteBufAtomic = new AtomicReference<>();

  PacketByteBufProvider(Packet packet) {
    this.packet = packet;
  }

  /**
   * @return
   * @throws IOException
   */
  ByteBuf retainedByteBuf() {
    if (byteBufAtomic.get() != null) {
      return byteBufAtomic.get().retainedDuplicate();
    }
    synchronized (byteBufAtomic) {
      if (byteBufAtomic.get() != null) {
        return byteBufAtomic.get();
      }
      ByteBuf byteBuf;
      try {
        byteBuf = PacketUtils.toByteBuf(packet);
      } catch (IOException e) {
        throw new BizCodeException(BizCodes.C3601, e);
      }
      byteBufAtomic.set(byteBuf);
      return byteBuf.retainedDuplicate();
    }
  }

  /** @return */
  ByteBuf obtainByteBuf() {
    return byteBufAtomic.get();
  }
}
