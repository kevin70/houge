package io.zhudy.xim.packet;

import lombok.Value;

/**
 * 错误消息.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Value
public class ErrorPacket implements OpsPacket {

  /** 错误信息描述. */
  String message;
  /** 错误信息详细信息. */
  String details;

  @Override
  public String getNs() {
    return Namespaces.ERROR;
  }
}
