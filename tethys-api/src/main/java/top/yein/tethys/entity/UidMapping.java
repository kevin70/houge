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
package top.yein.tethys.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * {@code uid_mappings} 表实体.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class UidMapping {

  /** 用户 ID. */
  private Long id;
  /** 被映射的用户 ID. */
  private String mappedUid;
  /** 创建时间. */
  private LocalDateTime createTime;
}
