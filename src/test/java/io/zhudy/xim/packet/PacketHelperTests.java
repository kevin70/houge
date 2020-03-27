package io.zhudy.xim.packet;

import com.fasterxml.jackson.core.JsonProcessingException;
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
