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
package top.yein.tethys.storage.data;

import com.github.javafaker.Faker;
import top.yein.tethys.entity.User;

/**
 * 测试数据.
 *
 * @author KK (kzou227@qq.com)
 */
public class TestData {

  static final Faker FAKER = new Faker();

  public static User newUser() {
    var e = new User();
    e.setId(0L);
    e.setOriginUid(FAKER.regexify("[a-zA-Z]{1,64}"));
    return e;
  }
}
