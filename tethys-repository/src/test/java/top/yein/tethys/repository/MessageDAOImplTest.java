package top.yein.tethys.repository;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.entity.Message;

/**
 * {@link MessageDAOImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageDAOImplTest extends AbstractTestRepository {

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

    var dao = new MessageDAOImpl(r2dbcClient);
    var p = dao.insert(entity);
    StepVerifier.create(p).expectNext(1).expectComplete().verify();
  }
}
