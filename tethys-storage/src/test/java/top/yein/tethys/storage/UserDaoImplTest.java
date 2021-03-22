package top.yein.tethys.storage;

import com.github.javafaker.Faker;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.entity.User;

/**
 * {@link UserDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class UserDaoImplTest extends AbstractTestDao {

  Faker faker = new Faker();

  private UserDaoImpl newUserDaoImpl() {
    return new UserDaoImpl(r2dbcClient);
  }

  @Test
  void insert() {
    var dao = newUserDaoImpl();
    var entity = new User();
    entity.setId(0L);
    entity.setOriginUid(faker.regexify("[a-z]{1,8}"));
    var p = dao.insert(entity);
    StepVerifier.create(p).expectNext(entity.getId()).expectComplete().verify();

    delete("users", Map.of("id", entity.getId()));
  }
}
