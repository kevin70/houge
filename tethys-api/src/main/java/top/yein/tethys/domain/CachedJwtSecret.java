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

import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import javax.crypto.SecretKey;
import lombok.Builder;
import lombok.Value;

/**
 * 缓存的 {@link top.yein.tethys.entity.JwtSecret}.
 *
 * @author KK (kzou227@qq.com)
 */
@Value
@Builder
public class CachedJwtSecret {

  /** JWT 密钥. */
  private String id;
  /** JWT 签名算法. */
  private SignatureAlgorithm algorithm;
  /** HMAC 密钥. */
  private SecretKey secretKey;
  /**
   * 删除数据的时间戳.
   *
   * <p>值不为 0 值表示行数据已被软删除.
   */
  private int deleted;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 更新时间. */
  private LocalDateTime updateTime;
}
