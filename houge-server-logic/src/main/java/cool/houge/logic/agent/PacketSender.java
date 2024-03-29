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
package cool.houge.logic.agent;

import cool.houge.logic.packet.Packet;
import java.util.List;

/**
 * Packet分发器接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface PacketSender {

  /**
   * 将消息包发送给指定的用户.
   *
   * @param uids 用户 IDs
   * @param packet 消息包
   */
  void sendToUser(List<Long> uids, Packet packet);

  /**
   * 将消息包发送给指定的群组.
   *
   * @param gids 群组 IDs
   * @param packet 消息包
   */
  void sendToGroup(List<Long> gids, Packet packet);

  /**
   * 将消息包发送给所有用户.
   *
   * @param packet 消息包
   */
  void sendToAll(Packet packet);
}
