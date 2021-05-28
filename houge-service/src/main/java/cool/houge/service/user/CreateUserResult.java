package cool.houge.service.user;

import lombok.Builder;
import lombok.Value;

/**
 * 创建用户.
 */
@Value
@Builder
public
class CreateUserResult {

  /**
   * 用户ID.
   */
  private Long uid;
}
