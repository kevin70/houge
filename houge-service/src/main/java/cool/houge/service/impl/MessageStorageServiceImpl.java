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
package cool.houge.service.impl;

import cool.houge.storage.MessageDao;
import cool.houge.storage.entity.Message;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import cool.houge.service.MessageStorageService;

/**
 * 消息存储服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class MessageStorageServiceImpl implements MessageStorageService {

  private final MessageDao messageDao;

  /**
   * 可被 IoC 容器使用的构造函数.
   *
   * @param messageDao 消息数据访问对象
   */
  @Inject
  public MessageStorageServiceImpl(MessageDao messageDao) {
    this.messageDao = messageDao;
  }

  @Override
  public Mono<Void> store(Message entity, List<Long> uids) {
    return messageDao.insert(entity, uids);
  }
}
