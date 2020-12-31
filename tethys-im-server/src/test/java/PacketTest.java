import java.io.IOException;
import org.junit.jupiter.api.Test;
import top.yein.tethys.util.JsonUtils;
import top.yein.tethys.packet.Packet;
import top.yein.tethys.packet.PrivateMessagePacket;

/**
 * @author KK (kzou227@qq.com)
 * @date 2020-12-29 11:56
 */
public class PacketTest {

  @Test
  void exec() throws IOException {
    var objectReader = JsonUtils.objectMapper().readerFor(Packet.class);
    var objectWriter = JsonUtils.objectMapper().writerFor(Packet.class);

    var privateMsgJson = "{\"@ns\":\"private.msg\",\"content\":\"Hello World!\"}";
    Packet packet = objectReader.readValue(privateMsgJson);
    System.out.println(packet);
    ((PrivateMessagePacket) packet).setTo("123");

    System.out.println(objectWriter.writeValueAsString(packet));

    privateMsgJson = "{\"@ns\":\"group.msg\",\"content\":\"Hello World!\"}";
    packet = objectReader.readValue(privateMsgJson);
    System.out.println(packet);
    System.out.println(objectWriter.writeValueAsString(packet));
  }
}
