package top.yein.tethys.storage.query;

import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.Nil;
import top.yein.tethys.storage.AbstractTestDao;
import top.yein.tethys.storage.GroupDaoImpl;
import top.yein.tethys.storage.data.TestData;

/**
 * {@link GroupQueryDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class GroupQueryDaoImplTest extends AbstractTestDao {

  private GroupDaoImpl newGroupDao() {
    return new GroupDaoImpl(r2dbcClient);
  }

  private GroupQueryDaoImpl newGroupQueryDao() {
    return new GroupQueryDaoImpl(r2dbcClient);
  }

  @Test
  void existsById() {
    var groupDao = newGroupDao();
    var groupQueryDao = newGroupQueryDao();
    var entity = TestData.newGroup();
    var idVar = new long[1];
    var p =
        groupDao
            .insert(entity)
            .doOnNext(id -> idVar[0] = id)
            .flatMap(id -> groupQueryDao.existsById(id));
    StepVerifier.create(p).expectNext(Nil.INSTANCE).expectComplete().verify();

    // 清理数据
    delete("groups", Map.of("id", idVar[0]));
  }

  @Test
  void noExistsById() {
    var groupQueryDao = newGroupQueryDao();
    var p = groupQueryDao.existsById(-1L);
    StepVerifier.create(p).expectComplete().verify();
  }
}
