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
package cool.houge.storage.impl.query;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import cool.houge.Nil;
import cool.houge.storage.AbstractTestDao;
import cool.houge.storage.data.TestData;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import cool.houge.storage.impl.UserDaoImpl;

/**
 * {@link UserQueryDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class UserQueryDaoImplTest extends AbstractTestDao {

  private UserDaoImpl newUserDao() {
    return new UserDaoImpl(r2dbcClient);
  }

  private UserQueryDaoImpl newUserQueryDao() {
    return new UserQueryDaoImpl(r2dbcClient);
  }

  @Test
  void queryById() {
    var userDao = newUserDao();
    var entity = TestData.newUser();
    var initMono = userDao.insert(entity);

    var userQueryDao = newUserQueryDao();
    var p = initMono.then(userQueryDao.queryById(entity.getId()));
    StepVerifier.create(p)
        .consumeNextWith(
            u ->
                assertSoftly(
                    s -> {
                      s.assertThat(u.getId()).as("id").isEqualTo(entity.getId());
                      s.assertThat(u.getOriginUid())
                          .as("origin_uid")
                          .isEqualTo(entity.getOriginUid());
                      s.assertThat(u.getCreateTime()).as("create_time").isNotNull();
                      s.assertThat(u.getUpdateTime()).as("update_time").isNotNull();
                    }))
        .expectComplete()
        .verify();

    // 清理数据
    delete("users", Map.of("id", entity.getId()));
  }

  @Test
  void queryById_NotFound() {
    var dao = newUserQueryDao();
    var p = dao.queryById(-1L);
    StepVerifier.create(p).expectComplete().verify();
  }

  @Test
  void existsBy() {
    var userDao = newUserDao();
    var entity = TestData.newUser();
    var initMono = userDao.insert(entity);

    var userQueryDao = newUserQueryDao();
    var p = initMono.then(userQueryDao.existsById(entity.getId()));
    StepVerifier.create(p).expectNext(Nil.INSTANCE).expectComplete().verify();

    // 清理数据
    delete("users", Map.of("id", entity.getId()));
  }

  @Test
  void existsBy_False() {
    var dao = newUserQueryDao();
    var p = dao.existsById(-1L);
    StepVerifier.create(p).expectComplete().verify();
  }
}
