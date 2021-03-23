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
package top.yein.tethys.storage;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javafaker.Faker;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import top.yein.tethys.entity.Group;
import top.yein.tethys.storage.data.TestData;

/**
 * {@link GroupDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class GroupDaoImplTest extends AbstractTestDao {

  private final Faker faker = new Faker();

  private GroupDaoImpl newGroupDao() {
    return new GroupDaoImpl(r2dbcClient);
  }

  private void clean(Long id) {
    delete("groups", Map.of("id", id));
    delete("groups_member", Map.of("gid", id));
  }

  @Test
  void insert() {
    var dao = newGroupDao();
    var entity = new Group();
    entity.setName("JUnit");
    entity.setCreatorId(faker.random().nextLong());
    entity.setOwnerId(entity.getCreatorId());
    entity.setMemberSize(1);
    entity.setMemberLimit(40);

    var p = dao.insert(entity);
    var idVar = new long[1];
    StepVerifier.create(p)
        .consumeNextWith(
            id -> {
              assertThat(id).isGreaterThanOrEqualTo(1);
              idVar[0] = id;
            })
        .expectComplete()
        .verify();

    // 清理数据
    clean(idVar[0]);
  }

  @Test
  void incMemberSize() {
    var groupDao = newGroupDao();
    var entity = TestData.newGroup();

    var idVar = new long[1];
    var p1 =
        groupDao
            .insert(entity)
            .delayUntil(
                id -> {
                  idVar[0] = id;
                  return Mono.empty();
                })
            .flatMap(id -> groupDao.incMemberSize(id, entity.getMemberLimit()));
    StepVerifier.create(p1).expectNext(1).expectComplete().verify();

    var p2 = groupDao.incMemberSize(idVar[0], entity.getMemberLimit() + 1);
    StepVerifier.create(p2).expectNext(0).expectComplete().verify();

    // 清理数据
    clean(idVar[0]);
  }

  @Test
  void decMemberSize() {
    var groupDao = newGroupDao();
    var entity = TestData.newGroup();
    entity.setMemberSize(10);

    var idVar = new long[1];
    var p1 =
        groupDao
            .insert(entity)
            .delayUntil(
                id -> {
                  idVar[0] = id;
                  return Mono.empty();
                })
            .flatMap(id -> groupDao.decMemberSize(id, entity.getMemberSize()));
    StepVerifier.create(p1).expectNext(1).expectComplete().verify();

    var p2 = groupDao.decMemberSize(idVar[0], entity.getMemberSize() + 1);
    StepVerifier.create(p2).expectNext(0).expectComplete().verify();

    // 清理数据
    clean(idVar[0]);
  }
}
