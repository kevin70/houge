package top.yein.tethys.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.entity.UidMapping;

/**
 * {@link UidMappingDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class UidMappingDaoImplTest extends AbstractTestDao {

  private UidMappingDao newUidMappingDao() {
    return new UidMappingDaoImpl(r2dbcClient);
  }

  @Test
  void insert() {
    var dao = newUidMappingDao();
    var entity = new UidMapping();
    entity.setMappedUid(UUID.randomUUID().toString());
    var p = dao.insert(entity);
    StepVerifier.create(p)
        .consumeNextWith(id -> assertThat(id).isGreaterThanOrEqualTo(1))
        .expectComplete()
        .verify();

    // 清理数据
    clean("delete from uid_mappings where mapped_uid=$1", new Object[] {entity.getMappedUid()});
  }
}
