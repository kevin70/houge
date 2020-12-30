package top.yein.tethys.core.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 访问令牌 DTO.
 *
 * @author KK (kzou227@qq.com)
 * @date 2020-12-30 15:36
 */
@Data
@Accessors(chain = true)
public class AccessTokenDto {

  /** 访问令牌. */
  private String accessToken;
}
