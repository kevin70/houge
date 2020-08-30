/*
 * Copyright 2019-2020 the original author or authors
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
package io.zhudy.xim.server;

import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.session.Session;
import reactor.core.publisher.Mono;

/**
 * 消息处理器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@FunctionalInterface
public interface PacketHandler {

  /**
   * 消息包处理器.
   *
   * @param packet 消息包
   * @param session 发送者会话
   * @return 结果
   */
  Mono<Void> handle(Packet packet, Session session);
}
