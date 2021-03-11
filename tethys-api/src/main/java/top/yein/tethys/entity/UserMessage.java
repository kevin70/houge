package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * {@code t_user_message} 用户消息关联表.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class UserMessage {

  /** 用户 ID. */
  private Long uid;
  /** 消息 ID. */
  private String messageId;
  /** 创建时间. */
  private LocalDateTime createTime;
}
