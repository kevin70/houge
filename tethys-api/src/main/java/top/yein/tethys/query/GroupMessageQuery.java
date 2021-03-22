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
package top.yein.tethys.query;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 群组消息查询对象.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class GroupMessageQuery {

  /** 群组 ID. */
  private String groupId;
  /** 消息创建时间. */
  private LocalDateTime createTime;
  /** 返回最大条数. */
  private int limit;
  /** 偏移量. */
  private int offset;
}
