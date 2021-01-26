package top.yein.tethys.domain;

import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import javax.crypto.SecretKey;
import lombok.Builder;
import lombok.Value;

/**
 * 缓存的 {@link top.yein.tethys.entity.JwtSecret}.
 *
 * @author KK (kzou227@qq.com)
 */
@Value
@Builder
public class CachedJwtSecret {

  /** JWT 密钥. */
  private String id;
  /** JWT 签名算法. */
  private SignatureAlgorithm algorithm;
  /** HMAC 密钥. */
  private SecretKey secretKey;
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
