package top.yein.tethys.domain;

import lombok.Value;

/**
 * 分页实体.
 *
 * @author KK (kzou227@qq.com)
 */
@Value
public class Paging {

  /** 偏移量. */
  private final int offset;
  /** 条数. */
  private final int limit;

  private Paging(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }

  /**
   * @param offset
   * @param limit
   * @return
   */
  public static Paging of(int offset, int limit) {
    return new Paging(offset, limit);
  }
}
