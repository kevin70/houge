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
package top.yein.tethys.vo;

import lombok.Data;

/**
 * 创建群组 VO.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class GroupCreateVo {

  /** 群组 ID. */
  private Long id;
  /** 创建者用户 ID. */
  private long creatorId;
  /** 群组名称. */
  private String name;
  /** 群成员数量限制. */
  private int memberLimit;
}
