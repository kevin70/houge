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

import reactor.core.publisher.Mono;
import top.yein.tethys.service.result.MessageSendResult;
import top.yein.tethys.vo.MessageSendVo;

/**
 * 远程消息服务接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface RemoteMessageService {

  /**
   * 发送消息至远程服务.
   *
   * @param senderId 发送者
   * @param vo VO
   * @return 发送消息响应
   */
  Mono<MessageSendResult> sendMessage(long senderId, MessageSendVo vo);
}
