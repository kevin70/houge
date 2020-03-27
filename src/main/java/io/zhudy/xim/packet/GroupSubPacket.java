package io.zhudy.xim.packet;

import java.util.Set;
import lombok.Value;

@Value
public class GroupSubPacket implements Packet {

  Set<String> groupIds;

  @Override
  public String getNs() {
    return Namespaces.GROUP_SUBSCRIBE;
  }
}
