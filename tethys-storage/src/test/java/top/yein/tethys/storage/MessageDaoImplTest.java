package top.yein.tethys.storage;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.entity.Message;

/**
 * {@link MessageDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageDaoImplTest extends AbstractTestDao {

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
}
