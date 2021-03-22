package top.yein.tethys.storage.data;

import com.github.javafaker.Faker;
import top.yein.tethys.entity.User;

/**
 * 测试数据.
 *
 * @author KK (kzou227@qq.com)
 */
public class TestData {

  static final Faker FAKER = new Faker();

  public static User newUser() {
    var e = new User();
    e.setId(0L);
    e.setOriginUid(FAKER.regexify("[a-zA-Z]{1,64}"));
    return e;
  }
}
