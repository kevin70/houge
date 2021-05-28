package cool.houge.service.group;

import lombok.Builder;
import lombok.Value;

/**
 * 加入群组请求参数对象.
 */
@Value
@Builder
public
class JoinMemberInput {

  /**
   * 群组ID.
   */
  private long gid;
  /**
   * 用户ID.
   */
  private long uid;
}
