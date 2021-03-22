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
package top.yein.tethys.service;

import reactor.core.publisher.Flux;
import top.yein.tethys.dto.GroupMessageDTO;
import top.yein.tethys.query.GroupMessageQuery;

/**
 * 群组聊天服务接口定义.
 *
 * @author KK (kzou227@qq.com)
 */
public interface GroupMessageService {

  /**
   * 查询群组消息.
   *
   * @param query 查询对象
   * @return 消息列表
   */
  Flux<GroupMessageDTO> findRecentMessages(GroupMessageQuery query);
}
