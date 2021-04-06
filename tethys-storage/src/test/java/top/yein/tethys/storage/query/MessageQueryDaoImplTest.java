/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.yein.tethys.storage.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import top.yein.tethys.domain.Paging;
import top.yein.tethys.storage.AbstractTestDao;
import top.yein.tethys.storage.MessageDaoImpl;
import top.yein.tethys.storage.data.TestData;
import top.yein.tethys.storage.entity.Message;

/**
 * {@link MessageQueryDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageQueryDaoImplTest extends AbstractTestDao {

  MessageDaoImpl newMessageDao() {
    return new MessageDaoImpl(r2dbcClient);
  }

  MessageQueryDaoImpl newMessageQueryDao() {
    return new MessageQueryDaoImpl(r2dbcClient);
  }

  @Test
  void queryById() {
    var messageDao = newMessageDao();
    var messageQueryDao = newMessageQueryDao();
    var entity = TestData.newMessage();
    var p =
        messageDao
            .insert(entity, List.of(entity.getSenderId(), entity.getReceiverId()))
            .then(messageQueryDao.queryById(entity.getId()));
    StepVerifier.create(p)
        .consumeNextWith(
            dbRow ->
                assertSoftly(
                    s -> {
                      s.assertThat(dbRow.getId()).as("id").isEqualTo(entity.getId());
                      s.assertThat(dbRow.getSenderId())
                          .as("sender_id")
                          .isEqualTo(entity.getSenderId());
                      s.assertThat(dbRow.getReceiverId())
                          .as("receiver_id")
                          .isEqualTo(entity.getReceiverId());
                      s.assertThat(dbRow.getGroupId())
                          .as("group_id")
                          .isEqualTo(entity.getGroupId());
                      s.assertThat(dbRow.getKind()).as("kind").isEqualTo(entity.getKind());
                      s.assertThat(dbRow.getContent()).as("content").isEqualTo(entity.getContent());
                      s.assertThat(dbRow.getContentType())
                          .as("content_type")
                          .isEqualTo(entity.getContentType());
                      s.assertThat(dbRow.getExtraArgs())
                          .as("extra_args")
                          .isEqualTo(entity.getExtraArgs());
                      s.assertThat(dbRow.getUnread()).as("unread").isZero();
                      s.assertThat(dbRow.getCreateTime()).as("create_time").isNotNull();
                      s.assertThat(dbRow.getUpdateTime()).as("update_time").isNotNull();
                    }));

    // 清理数据
    delete("messages", Map.of("id", entity.getId()));
    delete("user_messages", Map.of("message_id", entity.getId()));
  }

  @Test
  void queryByUser() {
    var messageDao = newMessageDao();
    var messageQueryDao = newMessageQueryDao();
    var uid = TestData.FAKER.random().nextLong();
    var entities = new ArrayList<Message>();
    var count = 30;
    for (int i = 0; i < count; i++) {
      var e = TestData.newMessage();
      e.setReceiverId(uid);
      entities.add(e);
    }

    var q =
        UserMessageQuery.builder().uid(uid).beginTime(LocalDateTime.now().minusHours(1)).build();
    var paging = Paging.of(0, 10);
    var p =
        Flux.fromIterable(entities)
            .flatMap(
                entity ->
                    messageDao.insert(
                        entity, List.of(entity.getSenderId(), entity.getReceiverId())))
            .thenMany(Flux.defer(() -> messageQueryDao.queryByUser(q, paging)));

    var messages = new ArrayList<Message>();
    StepVerifier.create(p)
        .recordWith(() -> messages)
        .thenConsumeWhile(message -> true)
        .expectComplete()
        .verify();

    assertThat(messages).as("messages_size").hasSize(paging.getLimit());
    assertThat(messages).flatExtracting(Message::getReceiverId).containsOnly(uid);

    // 清理数据
    for (Message message : messages) {
      delete("messages", Map.of("id", message.getId()));
      delete("user_messages", Map.of("message_id", message.getId()));
    }
  }
}
