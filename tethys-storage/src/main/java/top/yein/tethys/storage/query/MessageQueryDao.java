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
package top.yein.tethys.storage.query;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.domain.Paging;
import top.yein.tethys.storage.entity.Message;

/**
 * 消息查询数据访问接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageQueryDao {

  /**
   * 使用消息 ID 查询消息.
   *
   * @param id 消息 ID
   * @return 消息
   */
  Mono<Message> queryById(String id);

  /**
   * 分页查询符合用户指定条件的消息.
   *
   * @param q 指定的查询条件
   * @param paging 分页
   * @return
   */
  Flux<Message> queryByUser(UserMessageQuery q, Paging paging);
}
