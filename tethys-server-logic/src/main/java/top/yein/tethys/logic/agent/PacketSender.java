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
package top.yein.tethys.logic.agent;

import java.util.List;
import top.yein.tethys.logic.packet.Packet;

/**
 * Packet分发器接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface PacketSender {

  /**
   * @param uids
   * @param packet
   */
  void sendToUser(List<Long> uids, Packet packet);

  /**
   * @param gids
   * @param packet
   */
  void sendToGroup(List<Long> gids, Packet packet);

  /** @param packet */
  void sendToAll(Packet packet);
}
