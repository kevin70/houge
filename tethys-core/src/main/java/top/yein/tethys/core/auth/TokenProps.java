package top.yein.tethys.core.auth;

import lombok.Value;

/**
 * 令牌配置.
 *
 * @author KK (kzou227@qq.com)
 */
@Value
public class TokenProps {

  /** 生成令牌配置. */
  private Generator generator;

  /** 生成令牌配置. */
  @Value
  public static class Generator {

    /** 是否开启测试令牌生成. */
    private boolean testEnabled;
  }
}
