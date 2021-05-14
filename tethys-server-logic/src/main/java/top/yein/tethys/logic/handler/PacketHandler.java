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
package top.yein.tethys.logic.handler;

import reactor.core.publisher.Mono;
import top.yein.tethys.logic.packet.Packet;

/**
 * 消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public interface PacketHandler<T extends Packet> {

  /**
   * @param requestUid
   * @param packet
   * @return
   */
  Mono<Result> handle(long requestUid, T packet);
}
