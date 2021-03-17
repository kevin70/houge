package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * {@code uid_mappings} 表实体.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class UidMapping {

  /** 用户 ID. */
  private Long id;
  /** 被映射的用户 ID. */
  private String mappedUid;
  /** 创建时间. */
  private LocalDateTime createTime;
}
