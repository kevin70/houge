package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * t_private_message 数据实体.
 *
 * @author KK (kzou227@qq.com)
 * @date 2020-12-25 16:51
 */
@Data
public class PrivateMessage {

  /** 用户 ID. */
  private long uid;
  /** 消息 ID. */
  private long messageId;
  /** 发送人 ID. */
  private Long senderId;
  /** 接收人 ID. */
  private Long receiverId;
  /** 消息类型. */
  private int kind;
  /** 消息内容. */
  private String content;
  /** 统一资源定位器. */
  private String url;
  /** 自定义参数. */
  private String customArgs;
  /** 是否未读. */
  private int unread;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 修改时间. */
  private LocalDateTime updateTime;

}
