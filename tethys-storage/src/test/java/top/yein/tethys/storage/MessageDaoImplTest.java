package top.yein.tethys.storage;

import com.github.javafaker.Faker;
import com.google.common.base.Stopwatch;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.entity.Message;

/**
 * {@link MessageDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageDaoImplTest extends AbstractTestDao {

  private Faker faker = new Faker();

  @Test
  void insert() {
    var entity = new Message();
    entity.setId(TestUtils.newMessageId());
    entity.setSenderId(0L);
    entity.setReceiverId(0L);
    entity.setGroupId(0L);
    entity.setKind(1);
    entity.setContent("Hello JUnit Test");
    entity.setContentKind(1);
    entity.setUrl("https://gitee.com/kk70/tethys");
    entity.setCustomArgs("CUSTOM_ARGS");
    entity.setCreateTime(LocalDateTime.now());
    entity.setUpdateTime(LocalDateTime.now());

    var dao = new MessageDaoImpl(r2dbcClient);
    var p = dao.insert(entity);
    StepVerifier.create(p).expectNext(1).expectComplete().verify();
  }

  @DisplayName("保存消息与用户关系")
  @Test
  void insert2() {
    var entity = new Message();
    entity.setId(TestUtils.newMessageId());
    entity.setSenderId(faker.random().nextLong());
    entity.setReceiverId(faker.random().nextLong());
    entity.setGroupId(0L);
    entity.setKind(1);
    entity.setContent("Hello JUnit Test");
    entity.setContentKind(1);
    entity.setUrl("https://gitee.com/kk70/tethys");
    entity.setCustomArgs("CUSTOM_ARGS");

    var uids = new ArrayList<Long>();
    for (int i = 0; i < 10; i++) {
      uids.add(faker.random().nextLong());
    }

    var dao = new MessageDaoImpl(r2dbcClient);
    var stopwatch = Stopwatch.createStarted();
    var p = dao.insert(entity, uids);
    StepVerifier.create(p).expectComplete().verify();
    stopwatch.stop();
    System.out.println(stopwatch);
  }
}
