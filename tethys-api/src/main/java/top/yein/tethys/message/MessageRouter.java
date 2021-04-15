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
package top.yein.tethys.message;

import java.util.function.Predicate;
import reactor.core.publisher.Mono;
import top.yein.tethys.packet.MessagePacket;
import top.yein.tethys.session.Session;

/**
 * 消息路由接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageRouter {

  /**
   * 路由消息.
   *
   * @param packet 消息包
   * @param p 断言是否向 session 发送消息包
   * @return RS
   */
  Mono<Void> route(MessagePacket packet, Predicate<Session> p);
}
