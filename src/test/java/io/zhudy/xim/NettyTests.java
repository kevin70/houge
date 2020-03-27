package io.zhudy.xim;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** @author Kevin Zou (kevinz@weghst.com) */
public class NettyTests {

  @Test
  public void zeroCopy() {
    // zero copy
    var buf1 = ByteBufAllocator.DEFAULT.buffer();
    ByteBufUtil.writeUtf8(buf1, "hello");

    var buf2 = ByteBufAllocator.DEFAULT.buffer();
    ByteBufUtil.writeUtf8(buf2, " world!");

    buf1.writeBytes(buf2);
    buf1.writeBytes("HELLO".getBytes());

    var t = buf1.readCharSequence(2, StandardCharsets.UTF_8);
    System.out.println(t);

    var buf3 = buf1.resetReaderIndex().duplicate();
    buf1.duplicate();
    System.out.println(buf3.readCharSequence(2, StandardCharsets.UTF_8));
  }
}
