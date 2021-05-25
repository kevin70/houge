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

import com.github.javafaker.Faker;
import cool.houge.storage.AbstractTestDao;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import cool.houge.entity.User;

/**
 * {@link UserDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class UserDaoImplTest extends AbstractTestDao {

  Faker faker = new Faker();

  private UserDaoImpl newUserDaoImpl() {
    return new UserDaoImpl(r2dbcClient);
  }

  @Test
  void insert() {
    var dao = newUserDaoImpl();
    var entity = new User();
    entity.setId(0L);
    entity.setOriginUid(faker.regexify("[a-z]{1,8}"));
    var p = dao.insert(entity);
    StepVerifier.create(p).expectNext(entity.getId()).expectComplete().verify();

    delete("users", Map.of("id", entity.getId()));
  }
}
