/*
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
package io.zhudy.xim.packet;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.zhudy.xim.helper.PacketHelper;
import org.junit.jupiter.api.Test;

public class PacketHelperTests {

  @Test
  public void descMessagePacket() throws JsonProcessingException {
    var json2 =
        "{\"ns\":\"private.msg\",\"from\":\"abc\",\"to\":\"test\",\"text\":\"Hello World!\" }";

    for (int i = 0; i < 1; i++) {
      var o = PacketHelper.MAPPER.readValue(json2, Packet.class);
      System.out.println(o);
      System.out.println(PacketHelper.MAPPER.writeValueAsString(o));
    }
  }
}
