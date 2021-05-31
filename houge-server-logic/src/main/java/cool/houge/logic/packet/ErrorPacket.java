package cool.houge.logic.packet;

import lombok.Builder;
import lombok.Value;

/**
 * 错误包.
 *
 * @author KK (kzou227@qq.com)
 */
@Value
@Builder
public class ErrorPacket implements Packet {

  /** 错误码. */
  private int code;
  /** 错误描述. */
  private String message;
  /** 详细描述. */
  private Object details;

  @Override
  public String getNs() {
    return Packet.NS_ERROR;
  }
}
