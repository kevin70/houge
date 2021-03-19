package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群组信息.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

  /** 群组 ID. */
  private Long id;
  /** 群组名称. */
  private String name;
  /** 创建群的用户 ID. */
  private Long creatorId;
  /** 群持有者用户 ID. */
  private Long ownerId;
  /** 群成员数量. */
  private Integer memberSize;
  /** 群成员数量限制. */
  private Integer memberLimit;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 更新时间. */
  private LocalDateTime updateTime;

  /** 群组成员关系. */
  @Data
  @lombok.Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Member {

    /** 群组 ID. */
    private Long gid;
    /** 用户 ID. */
    private Long uid;
    /** 创建时间. */
    private LocalDateTime createTime;
  }
}
