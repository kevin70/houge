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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.util.PacketUtils;
import top.yein.tethys.packet.Packet;

/**
 * Packet 的 ByteBuf 提供者.
 *
 * @author KK (kzou227@qq.com)
 */
class PacketByteBufProvider {

  private final Packet packet;
  private volatile ByteBuf byteBuf;

  PacketByteBufProvider(@Nonnull Packet packet) {
    this.packet = packet;
  }

  /**
   * 返回消息包.
   *
   * @return 消息包
   */
  Packet getPacket() {
    return packet;
  }

  /**
   * 序列化 Packet 并返回 ByteBuf 引用复制实例.
   *
   * @return ByteBuf 引用复制实例
   */
  @Nonnull
  ByteBuf retainedByteBuf() {
    var bb = this.byteBuf;
    if (bb != null) {
      return bb.retainedDuplicate();
    }

    synchronized (packet) {
      if (this.byteBuf != null) {
        return this.byteBuf;
      }

      try {
        ByteBuf byteBuf = PacketUtils.toByteBuf(packet);
        this.byteBuf = byteBuf;
        return byteBuf.retainedDuplicate();
      } catch (IOException e) {
        throw new BizCodeException(BizCodes.C3601, e);
      }
    }
  }

  /**
   * 返回 ByteBuf 原始实例.
   *
   * @return ByteBuf 原始实例
   */
  @Nullable
  ByteBuf obtainByteBuf() {
    return byteBuf;
  }
}
