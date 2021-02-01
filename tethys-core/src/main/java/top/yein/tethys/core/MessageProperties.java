package top.yein.tethys.core;

import lombok.Value;

/**
 * 聊天系统静态配置.
 *
 * @author KK (kzou227@qq.com)
 */
@Value
public class MessageProperties {

  /** 是否自动填充消息 ID. */
  private boolean autofillId;
}
