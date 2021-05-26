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
package cool.houge.service;

import cool.houge.Nil;
import lombok.Builder;
import lombok.Value;
import reactor.core.publisher.Mono;

/**
 * 用户服务接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface UserService {

  /**
   * 创建用户.
   *
   * @param p 创建用户参数
   * @return 用户ID
   */
  Mono<CreateResult> create(Create p);

  /**
   * 判断指定用户是否存在.
   *
   * <p>如果用户存在则返回一个 {@code Mono<Null>} 实例可用 {@code Mono} 操作符进行消费, 反之则返回 {@code Mono.empty()}.
   *
   * @param uid 用户 ID
   * @return true/false
   */
  Mono<Nil> existsById(long uid);

  /** 创建用户. */
  @Value
  @Builder
  class Create {

    /** 用户ID. */
    private Long uid;
    /** 原系统用户 ID. */
    private String originUid;
  }

  /** 创建用户. */
  @Value
  @Builder
  class CreateResult {

    /** 用户ID. */
    private Long uid;
  }
}
