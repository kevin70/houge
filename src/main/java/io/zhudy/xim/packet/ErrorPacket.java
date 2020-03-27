package io.zhudy.xim.packet;

import lombok.Value;

@Value
public class ErrorPacket implements Packet {

  String message;
  String details;

  @Override
  public String getNs() {
    return Namespaces.ERROR;
  }
}
