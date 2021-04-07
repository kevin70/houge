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
