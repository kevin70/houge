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
package top.yein.tethys.packet;

import static top.yein.tethys.packet.Namespaces.NS_PING;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * 心跳请求消息.
 *
 * @author KK (kzou227@qq.com)
 */
@JsonTypeName(NS_PING)
public class PingPacket implements Packet {

  @Override
  public String getNs() {
    return NS_PING;
  }
}
