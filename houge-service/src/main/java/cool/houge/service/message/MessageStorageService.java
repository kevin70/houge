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

import cool.houge.model.Message;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * 消息存储服务接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageStorageService {

  /**
   * 存储消息包.
   *
   * @param entity 消息实体
   * @param uids 用户ID列表
   * @return RS
   */
  Mono<Void> store(Message entity, List<Long> uids);
}
