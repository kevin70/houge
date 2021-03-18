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
