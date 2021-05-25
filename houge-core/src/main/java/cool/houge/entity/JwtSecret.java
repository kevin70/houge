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
package cool.houge.entity;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * t_jwt_secret JWT 密钥配置.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class JwtSecret {

  /** kid 标识仅支持2个字符. */
  private String id;
  /** 签名算法名称. */
  private String algorithm;
  /** HMAC 密钥. */
  private ByteBuffer secretKey;
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
