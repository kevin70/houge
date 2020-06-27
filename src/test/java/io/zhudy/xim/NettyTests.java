/**
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
    buf1.writeBytes("HELLO".getBytes(StandardCharsets.UTF_8));

    var t = buf1.readCharSequence(2, StandardCharsets.UTF_8);
    System.out.println(t);

    var buf3 = buf1.resetReaderIndex().duplicate();
    buf1.duplicate();
    System.out.println(buf3.readCharSequence(2, StandardCharsets.UTF_8));
  }
}
