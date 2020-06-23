package io.zhudy.xim.packet;

import java.util.Set;
import lombok.Value;

/**
 * 订阅指定群组消息.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Value
public class GroupSubPacket implements OpsPacket {

  /** 订阅群组的 IDs. */
  Set<String> groupIds;

  @Override
  public String getNs() {
    return Namespaces.GROUP_SUBSCRIBE;
  }
}
