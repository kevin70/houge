package cool.houge.service.group;

import lombok.Builder;
import lombok.Value;

/**
 * 创建群组返回对象.
 */
@Value
@Builder
public
class CreateGroupResult {

  /**
   * 群组 ID.
   */
  private Long gid;
}
