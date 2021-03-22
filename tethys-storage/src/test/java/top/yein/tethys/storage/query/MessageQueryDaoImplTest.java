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

import org.junit.jupiter.api.Test;
import top.yein.tethys.storage.AbstractTestDao;

/**
 * {@link MessageQueryDaoImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageQueryDaoImplTest extends AbstractTestDao {

  MessageQueryDao newMessageQueryDao() {
    return new MessageQueryDaoImpl(r2dbcClient);
  }

  @Test
  void queryById() {
    var dao = newMessageQueryDao();
  }

  @Test
  void queryByUser() {

  }
}
