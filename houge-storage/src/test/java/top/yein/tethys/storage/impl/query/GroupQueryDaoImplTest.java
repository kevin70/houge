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
package top.yein.tethys.storage.impl.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.Nil;
import top.yein.tethys.storage.AbstractTestDao;
import top.yein.tethys.storage.impl.GroupDaoImpl;
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
  void queryById() {
    var groupDao = newGroupDao();
    var groupQueryDao = newGroupQueryDao();
    var entity = TestData.newGroup();
    var idVar = new long[1];
    var p =
        groupDao
            .insert(entity)
            .flatMap(
                id -> {
                  idVar[0] = id;
                  return groupQueryDao.queryById(id);
                });
    StepVerifier.create(p)
        .consumeNextWith(
            dbRow ->
                assertSoftly(
                    s -> {
                      s.assertThat(dbRow.getId()).as("id").isEqualTo(idVar[0]);
                      s.assertThat(dbRow.getCreatorId())
                          .as("creator_id")
                          .isEqualTo(entity.getCreatorId());
                      s.assertThat(dbRow.getOwnerId())
                          .as("owner_id")
                          .isEqualTo(entity.getOwnerId());
                      s.assertThat(dbRow.getMemberSize()).as("member_size").isPositive();
                      s.assertThat(dbRow.getCreateTime()).as("create_time").isNotNull();
                      s.assertThat(dbRow.getUpdateTime()).as("update_time").isNotNull();
                    }));

    // 清理数据
    delete("groups", Map.of("id", idVar[0]));
    delete("groups_member", Map.of("gid", idVar[0]));
  }

  @Test
  void queryUidByGid() {
    var groupDao = newGroupDao();
    var groupQueryDao = newGroupQueryDao();
    var entity = TestData.newGroup();

    var idVar = new long[1];
    var p =
        groupDao
            .insert(entity)
            .flatMapMany(
                id -> {
                  idVar[0] = id;
                  return groupQueryDao.queryUidByGid(id);
                });
    StepVerifier.create(p)
        .recordWith(() -> new ArrayList<>())
        .thenConsumeWhile(uid -> true)
        .consumeRecordedWith(uids -> assertThat(uids).contains(entity.getCreatorId()))
        .expectComplete()
        .verify();

    // 清理数据
    delete("groups", Map.of("id", idVar[0]));
    delete("groups_member", Map.of("gid", idVar[0]));
  }

  @Test
  void queryGidByUid() {
    var groupDao = newGroupDao();
    var groupQueryDao = newGroupQueryDao();
    var entity = TestData.newGroup();

    var idVar = new long[1];
    var p =
        groupDao
            .insert(entity)
            .doOnNext(id -> idVar[0] = id)
            .thenMany(groupQueryDao.queryGidByUid(entity.getCreatorId()));
    StepVerifier.create(p)
        .recordWith(() -> new ArrayList<>())
        .thenConsumeWhile(id -> true)
        .consumeRecordedWith(ids -> assertThat(ids).contains(idVar[0]))
        .expectComplete()
        .verify();

    // 清理数据
    delete("groups", Map.of("id", idVar[0]));
    delete("groups_member", Map.of("gid", idVar[0]));
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
    delete("groups_member", Map.of("gid", idVar[0]));
  }

  @Test
  void noExistsById() {
    var groupQueryDao = newGroupQueryDao();
    var p = groupQueryDao.existsById(-1L);
    StepVerifier.create(p).expectComplete().verify();
  }
}
