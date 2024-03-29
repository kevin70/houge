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
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.github.javafaker.Faker;
import cool.houge.storage.AbstractTestDao;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import cool.houge.model.JwtSecret;

/**
 * {@link JwtSecretDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class JwtSecretDaoImplTest extends AbstractTestDao {

  private Faker faker = new Faker(Locale.SIMPLIFIED_CHINESE);

  private JwtSecretDaoImpl newJwtSecretDao() {
    return new JwtSecretDaoImpl(r2dbcClient);
  }

  @Test
  void insert() {
    var dao = newJwtSecretDao();
    var entity = new JwtSecret();
    entity.setId(faker.random().hex());
    entity.setAlgorithm("HS512");
    entity.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var p = dao.insert(entity);
    StepVerifier.create(p).expectComplete().verify();

    StepVerifier.create(
            r2dbcClient
                .sql("select * from jwt_secrets where id=$1")
                .bind(0, entity.getId())
                .fetch()
                .one())
        .consumeNextWith(
            dbRow ->
                assertSoftly(
                    s -> {
                      s.assertThat(dbRow.get("id")).as("id").isEqualTo(entity.getId());
                      s.assertThat(dbRow.get("algorithm"))
                          .as("algorithm")
                          .isEqualTo(entity.getAlgorithm());
                      s.assertThat(dbRow.get("secret_key"))
                          .as("secret_key")
                          .isEqualTo(entity.getSecretKey());
                      s.assertThat(dbRow.get("deleted")).as("deleted").isEqualTo(0);
                      s.assertThat(dbRow.get("create_time")).as("create_time").isNotNull();
                      s.assertThat(dbRow.get("update_time")).as("update_time").isNotNull();
                    }))
        .expectComplete()
        .verify();

    // 清理数据
    clean("delete from jwt_secrets where id=$1", new Object[] {entity.getId()});
  }

  @Test
  void delete() {
    var dao = newJwtSecretDao();
    var entity = new JwtSecret();
    entity.setId(faker.random().hex());
    entity.setAlgorithm("HS512");
    entity.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var p = dao.insert(entity).then(dao.delete(entity.getId()));
    StepVerifier.create(p).expectComplete().verify();
  }

  @Test
  void findById() {
    var dao = newJwtSecretDao();
    var entity = new JwtSecret();
    entity.setId(faker.random().hex());
    entity.setAlgorithm("HS512");
    entity.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var p = dao.insert(entity).then(dao.findById(entity.getId()));
    StepVerifier.create(p)
        .consumeNextWith(
            dbRow -> {
              assertSoftly(
                  s -> {
                    s.assertThat(dbRow.getId()).as("id").isEqualTo(entity.getId());
                    s.assertThat(dbRow.getAlgorithm())
                        .as("algorithm")
                        .isEqualTo(entity.getAlgorithm());
                    s.assertThat(dbRow.getSecretKey())
                        .as("secret_key")
                        .isEqualTo(entity.getSecretKey());
                    s.assertThat(dbRow.getDeleted()).as("deleted").isEqualTo(0);
                    s.assertThat(dbRow.getCreateTime()).as("create_time").isNotNull();
                    s.assertThat(dbRow.getUpdateTime()).as("update_time").isNotNull();
                  });
            })
        .expectComplete()
        .verify();

    // 清理数据
    clean("delete from jwt_secrets where id=$1", new Object[] {entity.getId()});
  }

  @Test
  void findAll() {
    var dao = newJwtSecretDao();
    var entity1 = new JwtSecret();
    entity1.setId(faker.random().hex());
    entity1.setAlgorithm("HS512");
    entity1.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var entity2 = new JwtSecret();
    entity2.setId(faker.random().hex());
    entity2.setAlgorithm("HS512");
    entity2.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var p = dao.insert(entity1).then(dao.insert(entity2)).thenMany(dao.findAll());
    StepVerifier.create(p)
        .recordWith(ArrayList::new)
        .thenConsumeWhile(unused -> true)
        .consumeRecordedWith(
            jwtSecrets ->
                assertThat(jwtSecrets).extracting("id").contains(entity1.getId(), entity2.getId()))
        .expectComplete()
        .verify();

    // 清理数据
    clean(
        "delete from jwt_secrets where id in ($1,$2)",
        new Object[] {entity1.getId(), entity2.getId()});
  }
}
