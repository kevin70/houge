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

import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.Nil;
import top.yein.tethys.storage.AbstractTestDao;
import top.yein.tethys.storage.GroupDaoImpl;
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
  }

  @Test
  void noExistsById() {
    var groupQueryDao = newGroupQueryDao();
    var p = groupQueryDao.existsById(-1L);
    StepVerifier.create(p).expectComplete().verify();
  }
}
