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
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.entity.Group;

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
    clean("delete from groups where id=$1", new Object[] {id});
    clean("delete from groups_member where gid=$1", new Object[] {id});
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
}
