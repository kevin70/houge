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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群组信息.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

  /** 群组 ID. */
  private Long id;
  /** 创建群的用户 ID. */
  private Long creatorId;
  /** 群持有者用户 ID. */
  private Long ownerId;
  /** 群成员数量. */
  private Integer memberSize;
  /** 创建时间. */
  private LocalDateTime createTime;
  /** 更新时间. */
  private LocalDateTime updateTime;

  /** 群组成员关系. */
  @Data
  @lombok.Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Member {

    /** 群组 ID. */
    private Long gid;
    /** 用户 ID. */
    private Long uid;
    /** 创建时间. */
    private LocalDateTime createTime;
  }
}
