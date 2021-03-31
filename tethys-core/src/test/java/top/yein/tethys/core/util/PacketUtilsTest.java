package top.yein.tethys.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import io.netty.buffer.ByteBufAllocator;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import top.yein.tethys.packet.PingPacket;

/**
 * {@link PacketUtils} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class PacketUtilsTest {

  @Test
  void toByteBuf() throws IOException {
    var packet = new PingPacket();
    var byteBuf = PacketUtils.toByteBuf(packet);
    assertThat(byteBuf.release()).isTrue();
  }

  @Test
  void toByteBuf2() throws IOException {
    var packet = new PingPacket();
    var byteBuf = PacketUtils.toByteBuf(ByteBufAllocator.DEFAULT, packet);
    assertThat(byteBuf.release()).isTrue();
  }
}
