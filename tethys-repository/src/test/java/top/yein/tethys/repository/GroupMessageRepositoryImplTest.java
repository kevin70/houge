package top.yein.tethys.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.constants.MessageKind;
import top.yein.tethys.entity.GroupMessage;
import top.yein.tethys.query.GroupMessageQuery;

/**
 * {@link GroupMessageRepositoryImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class GroupMessageRepositoryImplTest extends AbstractTestRepository {

  @Test
  void store() {
    var storage = new GroupMessageRepositoryImpl();

    var entity = new GroupMessage();
    entity.setId(TestUtils.newMessageId());
    entity.setGid(0L);
    entity.setSenderId(UUID.randomUUID().toString());
    entity.setKind(MessageKind.TEXT.getCode());
    entity.setContent("unit test");
    entity.setUrl("https://via.placeholder.com/150");
    entity.setCustomArgs("{}");

    var p = super.transactional(storage.store(entity));
    StepVerifier.create(p).verifyComplete();

    // 校验数据库存储数据
    var dbRow =
        super.findOne("select * from t_group_message where id=$1", Map.of("$1", entity.getId()));
    assertSoftly(
        s -> {
          s.assertThat(dbRow.get("id")).as("id").isEqualTo(entity.getId());
          s.assertThat(dbRow.get("sender_id")).as("sender_id").isEqualTo(entity.getSenderId());
          s.assertThat(dbRow.get("kind")).as("kind").isEqualTo((short) entity.getKind());
          s.assertThat(dbRow.get("content")).as("content").isEqualTo(entity.getContent());
          s.assertThat(dbRow.get("url")).as("url").isEqualTo(entity.getUrl());
          s.assertThat(dbRow.get("custom_args"))
              .as("custom_args")
              .isEqualTo(entity.getCustomArgs());
          s.assertThat(dbRow.get("create_time")).as("create_time").isNotNull();
          s.assertThat(dbRow.get("update_time")).as("update_time").isNotNull();
        });
  }

  @Test
  void findById() {
    var storage = new GroupMessageRepositoryImpl();

    var entity = new GroupMessage();
    entity.setId(TestUtils.newMessageId());
    entity.setGid(0L);
    entity.setSenderId(UUID.randomUUID().toString());
    entity.setKind(MessageKind.TEXT.getCode());
    entity.setContent("unit test");
    entity.setUrl("https://via.placeholder.com/150");
    entity.setCustomArgs("{}");

    var p = super.transactional(storage.store(entity).thenMany(storage.findById(entity.getId())));
    StepVerifier.create(p)
        .assertNext(
            dbRow ->
                assertSoftly(
                    s -> {
                      s.assertThat(dbRow.getId()).as("id").isEqualTo(entity.getId());
                      s.assertThat(dbRow.getSenderId())
                          .as("sender_id")
                          .isEqualTo(entity.getSenderId());
                      s.assertThat(dbRow.getKind()).as("kind").isEqualTo((short) entity.getKind());
                      s.assertThat(dbRow.getContent()).as("content").isEqualTo(entity.getContent());
                      s.assertThat(dbRow.getUrl()).as("url").isEqualTo(entity.getUrl());
                      s.assertThat(dbRow.getCustomArgs())
                          .as("url")
                          .isEqualTo(entity.getCustomArgs());
                      s.assertThat(dbRow.getCreateTime()).as("create_time").isNotNull();
                      s.assertThat(dbRow.getUpdateTime()).as("update_time").isNotNull();
                    }))
        .verifyComplete();
  }

  @Test
  void findByGid() {
    var storage = new GroupMessageRepositoryImpl();

    var entity = new GroupMessage();
    entity.setId(TestUtils.newMessageId());
    entity.setGid(0L);
    entity.setSenderId(UUID.randomUUID().toString());
    entity.setKind(MessageKind.TEXT.getCode());
    entity.setContent("unit test");
    entity.setUrl("https://via.placeholder.com/150");
    entity.setCustomArgs("{}");

    var query = new GroupMessageQuery();
    query.setGid(entity.getGid());
    query.setCreateTime(LocalDateTime.now().minusSeconds(5));
    query.setLimit(10);

    var p = super.transactional(storage.store(entity).thenMany(storage.findByGid(query)));
    var messages = p.collectList().block(Duration.ofSeconds(5));
    assertThat(messages.size()).isGreaterThanOrEqualTo(1);

    System.out.println("find group messages ------------------");
    System.out.println(messages);
    System.out.println("find group messages ------------------");
  }
}
