package cool.houge.rest.controller.message;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import cool.houge.constants.MessageContentType;
import lombok.Data;

/** @author KK (kzou227@qq.com) */
@Data
public class SendMessageBody {

  /** 用户ID. */
  private long uid;
  /** 群组ID. */
  private long gid;
  /** 消息内容. */
  private String content;
  /**
   * 消息内容类型.
   *
   * @see MessageContentType
   */
  private int contentType;
  /** 扩展参数. */
  private @JsonUnwrapped String extraArgs;
}
