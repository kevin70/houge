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
package cool.houge.storage.query;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * 查询用户指定起始时间之后的消息.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
@Builder
public class UserMessageQuery {

  /** 用户 ID. */
  private long uid;
  /** 查询起始时间(包含). */
  private LocalDateTime beginTime;
}
