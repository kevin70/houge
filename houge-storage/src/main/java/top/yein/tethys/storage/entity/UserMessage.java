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
package top.yein.tethys.storage.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * {@code t_user_message} 用户消息关联表.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class UserMessage {

  /** 用户 ID. */
  private Long uid;
  /** 消息 ID. */
  private String messageId;
  /** 创建时间. */
  private LocalDateTime createTime;
}
