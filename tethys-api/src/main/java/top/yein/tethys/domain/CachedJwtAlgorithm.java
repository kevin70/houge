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
package top.yein.tethys.domain;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.Builder;
import lombok.Value;

/**
 * 缓存的 JWT 算法.
 *
 * <p>当前实现已支持的算法:
 *
 * <ul>
 *   <li>HS256
 *   <li>HS512
 * </ul>
 *
 * @author KK (kzou227@qq.com)
 */
@Value
@Builder
public class CachedJwtAlgorithm {

  /** kid 标识仅支持2个字符. */
  private String id;
  /** JWT 签名算法. */
  private Algorithm algorithm;
  /**
   * 删除数据的时间戳.
   *
   * <p>值不为 0 值表示行数据已被软删除.
   */
  private int deleted;
}
