package top.yein.tethys.packet;

import static top.yein.tethys.packet.Namespaces.NS_PONG;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * 心跳响应消息.
 *
 * @author KK (kzou227@qq.com)
 */
@JsonTypeName(NS_PONG)
public class PongPacket implements Packet {

  @Override
  public String getNs() {
    return NS_PONG;
  }
}
