package top.yein.tethys.storage;

import reactor.core.publisher.Mono;
import top.yein.tethys.entity.UidMapping;

/**
 * {@code uid_mappings} 表数据访问类.
 *
 * @author KK (kzou227@qq.com)
 */
public interface UidMappingDao {

  /**
   * 保存用户 ID 映射关系.
   *
   * @param entity 实体
   * @return 用户 ID
   */
  Mono<Long> insert(UidMapping entity);

  /**
   * 根据被映射的用户 ID 查询用户映射实体.
   *
   * @param mappedUid 被映射的用户 ID
   * @return 映射实体
   */
  Mono<UidMapping> findByMappedUid(String mappedUid);
}
