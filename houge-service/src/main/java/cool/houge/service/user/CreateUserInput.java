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
package cool.houge.service.user;

import lombok.Builder;
import lombok.Value;

/**
 * 创建用户.
 */
@Value
@Builder
public
class CreateUserInput {

  /**
   * 用户ID.
   */
  private Long uid;
  /**
   * 原系统用户 ID.
   */
  private String originUid;
}
