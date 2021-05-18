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
package cool.houge.id;

import reactor.core.publisher.Flux;

/**
 * 消息 ID 生成器.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageIdGenerator {

  /** 单次请求消息 ID 的返回的最大个数. */
  int REQUEST_IDS_LIMIT = 100;

  /**
   * 生成一个 ID.
   *
   * @return ID
   */
  String nextId();

  /**
   * 生成一批 IDs.
   *
   * <p>通过 {@link Flux#limitRequest(long)} 设置请求的 ID 数量，如果请求值超过 {@link #REQUEST_IDS_LIMIT} 则返回 {@link
   * #REQUEST_IDS_LIMIT} 数量的 ID.
   *
   * @return IDs
   */
  Flux<String> nextIds();
}
