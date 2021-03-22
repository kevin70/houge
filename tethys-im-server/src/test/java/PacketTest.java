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
import java.io.IOException;
import org.junit.jupiter.api.Test;
import top.yein.tethys.packet.Packet;
import top.yein.tethys.packet.PrivateMessagePacket;
import top.yein.tethys.util.JsonUtils;

/**
 * {@link Packet} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
public class PacketTest {

  @Test
  void exec() throws IOException {
    var objectReader = JsonUtils.objectMapper().readerFor(Packet.class);
    var objectWriter = JsonUtils.objectMapper().writerFor(Packet.class);

    var privateMsgJson = "{\"@ns\":\"p.msg\",\"content\":\"Hello World!\"}";
    Packet packet = objectReader.readValue(privateMsgJson);
    System.out.println(packet);
    ((PrivateMessagePacket) packet).setTo(123);

    System.out.println(objectWriter.writeValueAsString(packet));

    privateMsgJson = "{\"@ns\":\"g.msg\",\"content\":\"Hello World!\"}";
    packet = objectReader.readValue(privateMsgJson);
    System.out.println(packet);
    System.out.println(objectWriter.writeValueAsString(packet));
  }
}
