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
package io.zhudy.xim.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zhudy.xim.packet.Packet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class PacketHelperTests {

  @Test
  void descMessagePacket() throws JsonProcessingException {
    var json2 =
        "{\"ns\":\"private.msg\",\"from\":\"abc\",\"to\":\"test\",\"content\":\"Hello World!\" }";

    for (int i = 0; i < 1; i++) {
      var o = PacketHelper.getObjectMapper().readValue(json2, Packet.class);
      System.out.println(o);
      System.out.println(PacketHelper.getObjectMapper().writeValueAsString(o));
    }
  }

  @Test
  void getObjectMapper() {
    var objectMapper = PacketHelper.getObjectMapper();
    assertThat(objectMapper).isNotNull();
  }

  @Test
  void getMockObjectMapper() {
    var objectMapper = Mockito.spy(ObjectMapper.class);
    try (var mockedStatic = Mockito.mockStatic(PacketHelper.class)) {
      mockedStatic.when(PacketHelper::getObjectMapper).thenReturn(objectMapper);
      assertThat(PacketHelper.getObjectMapper()).isEqualTo(objectMapper);
    }
  }
}
