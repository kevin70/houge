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
package cool.houge.service.message;

import cool.houge.domain.Paging;
import cool.houge.model.Message;
import cool.houge.storage.query.UserMessageQuery;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 消息服务接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageService {

  /**
   * 将指定用户的消息批量设置为已读状态.
   *
   * @param uid 用户 ID
   * @param messageIds 消息 IDs
   * @return RS
   */
  Mono<Void> readMessages(long uid, List<String> messageIds);

  /**
   * 分页查询符合用户指定条件的消息.
   *
   * @param q 指定的查询条件
   * @param paging 分页
   * @return
   */
  Flux<Message> queryByUser(UserMessageQuery q, Paging paging);
}
