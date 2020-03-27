package io.zhudy.xim.packet;

import lombok.Value;

@Value
public class GroupMsgPacket implements Packet {

  String from;
  String to;
  String text;
  String extraArgs;

  @Override
  public String getNs() {
    return Namespaces.GROUP_MSG;
  }
}
