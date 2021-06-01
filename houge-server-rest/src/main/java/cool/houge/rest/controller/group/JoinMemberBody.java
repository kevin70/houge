package cool.houge.rest.controller.group;

import lombok.Data;

/**
 * 群组加入成员请求数据.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class JoinMemberBody {

  /** 用户ID. */
  private long uid;
}
