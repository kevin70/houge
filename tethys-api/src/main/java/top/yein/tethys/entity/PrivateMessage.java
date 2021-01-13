package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * t_private_message 数据实体.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class PrivateMessage {

  /** 消息 ID 全局唯一. */
  private String id;
  /** 发送人 ID. */
  private String senderId;
  /** 接收人 ID. */
  private String receiverId;
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
