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
package top.yein.tethys.core.util;

import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import top.yein.tethys.packet.Packet;
import top.yein.tethys.util.JsonUtils;

/**
 * {@link Packet} 工具类.
 *
 * @author KK (kzou227@qq.com)
 */
public class PacketUtils {

  private static final ObjectWriter OBJECT_WRITER =
      JsonUtils.objectMapper().writerFor(Packet.class);

  /**
   * 将 Packet 做 JSON 序列化操作, 使用指定的 {@link ByteBufAllocator#DEFAULT} 分配直接的 buffer 写入 JSON 字节数据.
   *
   * <p>注意: 调用该方法序列化 Packet 需要手动释放 {@code ByteBuf}.
   *
   * @param packet 消息包
   * @return 直接的 buffer JSON 字节数据
   * @throws IOException 序列化失败
   */
  public static ByteBuf toByteBuf(Packet packet) throws IOException {
    return toByteBuf(ByteBufAllocator.DEFAULT, packet);
  }

  /**
   * 将 Packet 做 JSON 序列化操作, 使用指定的 {@code ByteBufAllocator} 分配直接的 buffer 写入 JSON 字节数据.
   *
   * <p>注意: 调用该方法序列化 Packet 需要手动释放 {@code ByteBuf}.
   *
   * @param allocator {@link ByteBufAllocator}
   * @param packet 消息包
   * @return 直接的 buffer JSON 字节数据
   * @throws IOException 序列化失败
   */
  public static ByteBuf toByteBuf(ByteBufAllocator allocator, Packet packet) throws IOException {
    ByteBufOutputStream out = new ByteBufOutputStream(allocator.buffer());
    OBJECT_WRITER.writeValue((DataOutput) out, packet);
    out.flush();
    out.close();
    return out.buffer();
  }
}
