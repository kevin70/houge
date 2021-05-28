package cool.houge.service.user;

import lombok.Builder;
import lombok.Value;

/**
 * 创建用户.
 */
@Value
@Builder
public
class CreateUserInput {

  /**
   * 用户ID.
   */
  private Long uid;
  /**
   * 原系统用户 ID.
   */
  private String originUid;
}
