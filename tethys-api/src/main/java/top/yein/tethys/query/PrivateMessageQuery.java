package top.yein.tethys.query;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 私聊消息查询对象.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class PrivateMessageQuery {

  /** 接收人 ID. */
  private String receiverId;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 返回最大条数. */
  private int limit;
  /** 偏移量. */
  private int offset;
}
