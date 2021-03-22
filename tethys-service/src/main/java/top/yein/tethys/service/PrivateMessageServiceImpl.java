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

import java.time.Duration;
import java.time.LocalDateTime;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.dto.PrivateMessageDTO;
import top.yein.tethys.query.PrivateMessageQuery;
import top.yein.tethys.vo.BatchReadMessageVO;

/**
 * 私聊服务对象.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class PrivateMessageServiceImpl implements PrivateMessageService {

  /**
   * 私人消息查询时间限制.
   *
   * <p>仅可查询72小时内的消息.
   */
  private static final int FIND_MESSAGE_TIME_LIMIT = 72;

  /** 构造函数. */
  @Inject
  public PrivateMessageServiceImpl() {}

  /**
   * 查询.
   *
   * @param query 查询对象
   */
  @Override
  public Flux<PrivateMessageDTO> findRecentMessages(PrivateMessageQuery query) {
    var createTime = query.getCreateTime();
    var now = LocalDateTime.now();
    var d = Duration.between(createTime, now);
    if (d.toHours() > FIND_MESSAGE_TIME_LIMIT) {
      var newTime = now.minusHours(FIND_MESSAGE_TIME_LIMIT);
      log.warn(
          "查询[{}]私人消息时间[{}]已超过{}小时，将查询时间重置为[{}]",
          query.getReceiverId(),
          createTime,
          FIND_MESSAGE_TIME_LIMIT,
          newTime);
      query.setCreateTime(newTime);
    }
    //    return privateMessageRepository
    //        .findMessages(query)
    //        .map(PrivateMessageMapper.INSTANCE::toPrivateMessageDTO);
    return Flux.empty();
  }

  @Override
  public Mono<Void> batchReadMessage(BatchReadMessageVO vo, String receiverId) {
    //    return privateMessageRepository
    //        .batchReadMessage(vo.getMessageIds(), receiverId)
    //        .doOnSuccess(
    //            n -> log.debug("用户[{}]批量将消息{}设置为已读状态，已修改{}行", receiverId, vo.getMessageIds(), n))
    //        .then();
    return Mono.empty();
  }
}
