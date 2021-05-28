package cool.houge.service.group;

import lombok.Builder;
import lombok.Value;

/** 创建群组对象. */
@Value
@Builder
public class CreateGroupInput {

  /** 群组 ID. */
  private Long gid;
  /** 创建者用户 ID. */
  private long creatorId;
  /** 群组名称. */
  private String name;
}
