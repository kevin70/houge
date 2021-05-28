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
package cool.houge.storage.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javafaker.Faker;
import com.google.common.base.Stopwatch;
import cool.houge.constants.MessageReadStatus;
import cool.houge.storage.AbstractTestDao;
import cool.houge.storage.TestUtils;
import cool.houge.storage.data.TestData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import cool.houge.model.Message;

/**
 * {@link MessageDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageDaoImplTest extends AbstractTestDao {

  private Faker faker = new Faker();

  private MessageDaoImpl newMessageDao() {
    return new MessageDaoImpl(r2dbcClient);
  }

  @Test
  void insert() {
    var messageDao = new MessageDaoImpl(r2dbcClient);
    var entity = TestData.newMessage();
    var p = messageDao.insert(entity, List.of(entity.getSenderId(), entity.getReceiverId()));
    StepVerifier.create(p).expectComplete().verify();
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
    entity.setContentType(1);
    entity.setExtraArgs("CUSTOM_ARGS");

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

  @Test
  void updateUnreadStatus() {
    var messageDao = newMessageDao();
    var entity1 = TestData.newMessage();
    var entity2 = TestData.newMessage();
    var readStatus = MessageReadStatus.READ.getCode();

    // 修改成功
    var p1 =
        messageDao
            .insert(entity1, List.of(entity1.getSenderId(), entity2.getReceiverId()))
            .then(
                messageDao.updateUnreadStatus(
                    entity1.getReceiverId(), List.of(entity1.getId()), readStatus));
    StepVerifier.create(p1).expectComplete().verify();
    var findSql = "select * from messages where id=$1";
    StepVerifier.create(r2dbcClient.sql(findSql).bind(0, entity1.getId()).fetch().one())
        .consumeNextWith(
            dbRow -> {
              var unread = (Short) dbRow.get("unread");
              assertThat(unread.intValue()).isEqualTo(readStatus);
            })
        .expectComplete()
        .verify();

    // 不能修改成功
    var p2 =
        messageDao
            .insert(entity2, List.of(entity2.getSenderId(), entity2.getReceiverId()))
            .then(
                messageDao.updateUnreadStatus(
                    entity1.getReceiverId(), List.of(entity2.getId()), readStatus));
    StepVerifier.create(p2).expectComplete().verify();
    StepVerifier.create(r2dbcClient.sql(findSql).bind(0, entity2.getId()).fetch().one())
        .consumeNextWith(
            dbRow -> {
              var unread = (Short) dbRow.get("unread");
              assertThat(unread.intValue()).isEqualTo(MessageReadStatus.READ.getCode());
            })
        .expectComplete()
        .verify();

    // 清理数据
    delete("messages", Map.of("id", entity1.getId()));
    delete("messages", Map.of("id", entity2.getId()));
  }
}
