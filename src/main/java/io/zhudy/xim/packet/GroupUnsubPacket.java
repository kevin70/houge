package io.zhudy.xim.packet;

import java.util.Set;
import lombok.Value;

@Value
public class GroupUnsubPacket implements Packet {

  Set<String> groupIds;

  @Override
  public String getNs() {
    return Namespaces.GROUP_UNSUBSCRIBE;
  }
}
