package top.yein.tethys.storage;

import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.UidMapping;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * {@code uid_mappings} 数据访问实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class UidMappingDaoImpl implements UidMappingDao {

  private static final String INSERT_SQL =
      "INSERT INTO uid_mappings(mapped_uid,create_time) VALUES($1,now())";

  private final R2dbcClient rc;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public UidMappingDaoImpl(R2dbcClient rc) {
    this.rc = rc;
  }

  @Override
  public Mono<Long> insert(UidMapping entity) {
    return rc.sql(INSERT_SQL)
        .bind(0, entity.getMappedUid())
        .returnGeneratedValues("id")
        .map(row -> row.get("id", Long.class))
        .one();
  }

  @Override
  public Mono<UidMapping> findByMappedId(String mappedUid) {
    return null;
  }
}
