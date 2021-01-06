package top.yein.tethys.core.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 访问令牌 DTO.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
@Accessors(chain = true)
public class AccessTokenDto {

  /** 访问令牌. */
  private String accessToken;
}
