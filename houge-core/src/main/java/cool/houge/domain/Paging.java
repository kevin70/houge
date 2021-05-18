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
package cool.houge.domain;

import lombok.Value;

/**
 * 分页实体.
 *
 * @author KK (kzou227@qq.com)
 */
@Value
public class Paging {

  /** 偏移量. */
  private final int offset;
  /** 条数. */
  private final int limit;

  private Paging(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }

  /**
   * @param offset
   * @param limit
   * @return
   */
  public static Paging of(int offset, int limit) {
    return new Paging(offset, limit);
  }
}
