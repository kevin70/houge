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
package io.zhudy.xim.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import io.zhudy.xim.packet.GroupMsgPacket;
import io.zhudy.xim.packet.GroupSubPacket;
import io.zhudy.xim.packet.GroupUnsubPacket;
import io.zhudy.xim.packet.Namespaces;
import io.zhudy.xim.packet.PrivateMsgPacket;

/**
 * Packet 工具类.
 *
 * <p>该工具类仅为 {@code Packet} 类型服务, 非 Packet 类型不应该使用此类.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public final class PacketHelper {

  private PacketHelper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Packet JSON 序列化与反序列化实例.
   *
   * <p>注意: <b>该对象经过特殊的配置, 序列化与反序列化非 {@code Packet} 对象时无法保证其正确性.</b>
   */
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    OBJECT_MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY);

    // 注册 Packet 子类以解析实际的包体
    OBJECT_MAPPER.registerSubtypes(new NamedType(PrivateMsgPacket.class, Namespaces.PRIVATE_MSG));
    OBJECT_MAPPER.registerSubtypes(new NamedType(GroupMsgPacket.class, Namespaces.GROUP_MSG));
    OBJECT_MAPPER.registerSubtypes(new NamedType(GroupSubPacket.class, Namespaces.GROUP_SUBSCRIBE));
    OBJECT_MAPPER.registerSubtypes(
        new NamedType(GroupUnsubPacket.class, Namespaces.GROUP_UNSUBSCRIBE));
  }

  /**
   * 返回 {@link ObjectMapper} 对象.
   *
   * <p>该方法返回的实例用于序列化、反序列化 {@link io.zhudy.xim.packet.Packet} JSON 对象.
   *
   * @return {@link ObjectMapper}
   */
  public static ObjectMapper getObjectMapper() {
    return OBJECT_MAPPER;
  }
}
