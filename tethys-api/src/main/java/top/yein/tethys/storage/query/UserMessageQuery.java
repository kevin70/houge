package top.yein.tethys.storage.query;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

/**
 * 查询用户指定起始时间之后的消息.
 *
 * @author KK (kzou227@qq.com)
 */
@Value
@Builder
public class UserMessageQuery {

  /** 用户 ID. */
  private long uid;
  /** 查询起始时间(包含). */
  private LocalDateTime beginTime;
}
