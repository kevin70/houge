package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * t_group_message 群组消息.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessage {

  /** 消息 ID 全局唯一. */
  private String id;
  /** 群组 ID. */
  private String groupId;
  /** 发送人 ID. */
  private String senderId;
  /** 消息类型. */
  private int kind;
  /** 消息内容. */
  private String content;
  /** 统一资源定位器. */
  private String url;
  /** 自定义参数. */
  private String customArgs;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 修改时间. */
  private LocalDateTime updateTime;
}
