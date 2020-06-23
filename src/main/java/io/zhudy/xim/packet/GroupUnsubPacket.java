package io.zhudy.xim.packet;

import java.util.Set;
import lombok.Value;

/**
 * 取消订阅指定群组消息.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Value
public class GroupUnsubPacket implements OpsPacket {

  /** 取消订阅群组的 IDs. */
  Set<String> groupIds;

  @Override
  public String getNs() {
    return Namespaces.GROUP_UNSUBSCRIBE;
  }
}
