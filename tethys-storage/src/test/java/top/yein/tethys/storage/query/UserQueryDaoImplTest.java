package top.yein.tethys.storage.query;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.storage.AbstractTestDao;
import top.yein.tethys.storage.UserDaoImpl;
import top.yein.tethys.storage.data.TestData;

/**
 * {@link UserQueryDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class UserQueryDaoImplTest extends AbstractTestDao {

  private UserDaoImpl newUserDao() {
    return new UserDaoImpl(r2dbcClient);
  }

  private UserQueryDaoImpl newUserQueryDao() {
    return new UserQueryDaoImpl(r2dbcClient);
  }

  @Test
  void queryById() {
    var userDao = newUserDao();
    var entity = TestData.newUser();
    var initMono = userDao.insert(entity);

    var userQueryDao = newUserQueryDao();
    var p = initMono.then(userQueryDao.queryById(entity.getId()));
    StepVerifier.create(p)
        .consumeNextWith(
            u ->
                assertSoftly(
                    s -> {
                      s.assertThat(u.getId()).as("id").isEqualTo(entity.getId());
                      s.assertThat(u.getOriginUid())
                          .as("origin_uid")
                          .isEqualTo(entity.getOriginUid());
                      s.assertThat(u.getCreateTime()).as("create_time").isNotNull();
                      s.assertThat(u.getUpdateTime()).as("update_time").isNotNull();
                    }))
        .expectComplete()
        .verify();

    // 清理数据
    delete("users", Map.of("id", entity.getId()));
  }

  @Test
  void queryById_NotFound() {
    var dao = newUserQueryDao();
    var p = dao.queryById(-1L);
    StepVerifier.create(p).expectComplete().verify();
  }

  @Test
  void existsBy() {
    var userDao = newUserDao();
    var entity = TestData.newUser();
    var initMono = userDao.insert(entity);

    var userQueryDao = newUserQueryDao();
    var p = initMono.then(userQueryDao.existsById(entity.getId()));
    StepVerifier.create(p).expectNext(true).expectComplete().verify();

    // 清理数据
    delete("users", Map.of("id", entity.getId()));
  }

  @Test
  void existsBy_False() {
    var dao = newUserQueryDao();
    var p = dao.existsById(-1L);
    StepVerifier.create(p).expectNext(false).expectComplete().verify();
  }
}
