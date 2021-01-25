package top.yein.tethys.entity;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * t_jwt_secret JWT 密钥配置.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class JwtSecret {

  /** kid 标识仅支持2个字符. */
  private String id;
  /** 签名算法名称. */
  private String algorithm;
  /** HMAC 密钥. */
  private ByteBuffer secretKey;
  /**
   * 删除数据的时间戳.
   *
   * <p>值不为 0 值表示行数据已被软删除.
   */
  private int deleted;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 更新时间. */
  private LocalDateTime updateTime;
}
