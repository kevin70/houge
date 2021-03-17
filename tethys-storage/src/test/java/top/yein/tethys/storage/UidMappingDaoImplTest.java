package top.yein.tethys.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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

  private void cleanByMappedUid(String mappedUid) {
    clean("delete from uid_mappings where mapped_uid=$1", new Object[] {mappedUid});
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
    cleanByMappedUid(entity.getMappedUid());
  }

  @Test
  void findByMappedUid() {
    var dao = newUidMappingDao();
    var entity = new UidMapping();
    entity.setMappedUid(UUID.randomUUID().toString());
    var p = dao.insert(entity).then(dao.findByMappedUid(entity.getMappedUid()));

    StepVerifier.create(p)
        .consumeNextWith(
            dbRow ->
                assertSoftly(
                    s -> {
                      s.assertThat(dbRow.getId()).as("id").isGreaterThanOrEqualTo(1);
                      s.assertThat(dbRow.getMappedUid())
                          .as("mapped_uid")
                          .isEqualTo(entity.getMappedUid());
                      s.assertThat(dbRow.getCreateTime()).as("create_time").isNotNull();
                    }))
        .expectComplete()
        .verify();

    // 清理数据
    cleanByMappedUid(entity.getMappedUid());
  }
}
