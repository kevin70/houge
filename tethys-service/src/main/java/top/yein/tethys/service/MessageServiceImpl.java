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

import java.time.LocalDateTime;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import top.yein.tethys.domain.Paging;
import top.yein.tethys.entity.Message;
import top.yein.tethys.storage.query.MessageQueryDao;
import top.yein.tethys.storage.query.UserMessageQuery;

/**
 * 消息服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class MessageServiceImpl implements MessageService {

  private final MessageProps messageProps;
  private final MessageQueryDao messageQueryDao;

  /**
   * @param messageProps
   * @param messageQueryDao
   */
  @Inject
  public MessageServiceImpl(MessageProps messageProps, MessageQueryDao messageQueryDao) {
    this.messageProps = messageProps;
    this.messageQueryDao = messageQueryDao;
  }

  @Override
  public Flux<Message> queryByUser(UserMessageQuery q, Paging paging) {
    var beginTimeLimit = LocalDateTime.now().minus(messageProps.getPullBeginTimeLimit());
    if (q.getBeginTime() == null || beginTimeLimit.isAfter(q.getBeginTime())) {
      q.setBeginTime(beginTimeLimit);
    }

    return messageQueryDao.queryByUser(q, paging);
  }
}
