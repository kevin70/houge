package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * {@code t_message} 消息表.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class Message {

  /** 消息 ID. */
  private String id;
  /** 发送人 ID. */
  private Long senderId;
  /** 接收人 ID. */
  private Long receiverId;
  /** 群主 ID. */
  private Long groupId;
  /**
   * 消息类型.
   *
   * <ul>
   *   <li>{@code 1}: 系统消息
   *   <li>{@code 2}: 私聊消息
   *   <li>{@code 3}: 群组消息
   * </ul>
   */
  private Integer kind;
  /** 消息内容. */
  private String content;
  /**
   * 消息内容类型.
   *
   * <ul>
   *   <li>{@code 1}: 普通文本消息
   *   <li>{@code 2}: 图片消息
   *   <li>{@code 3}: 音频消息
   *   <li>{@code 4}: 视频消息
   * </ul>
   */
  private Integer contentKind;
  /** 消息内容资源. */
  private String url;
  /** 自定义参数. */
  private String customArgs;
  /**
   * 消息是否未读.
   *
   * <ul>
   *   <li>{@code 1}: 未读
   *   <li>{@code 2}: 已读
   * </ul>
   */
  private Integer unread;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 更新时间. */
  private LocalDateTime updateTime;
}
