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
package top.yein.tethys.im.server;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.packet.ErrorPacket;
import top.yein.tethys.packet.Packet;
import top.yein.tethys.session.Session;

/**
 * Packet 分发器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class PacketDispatcher {

  private final Map<String, PacketHandler> handlers;

  /**
   * 使用 Guice Injector 构建对象.
   *
   * @param injector Guice Injector
   */
  @Inject
  public PacketDispatcher(Injector injector) {
    this(findPacketHandlers(injector));
  }

  /** @param handlers */
  public PacketDispatcher(Map<String, PacketHandler> handlers) {
    this.handlers = handlers;
  }

  /**
   * Packet 分发器.
   *
   * @param session 登录会话
   * @param packet packet
   * @return RS
   */
  public Mono<Void> dispatch(@Nonnull Session session, @Nonnull Packet packet) {
    var handler = handlers.get(packet.getNs());
    if (handler == null) {
      log.error("未找到 Packet[@ns={}] 实现 {}", packet.getNs(), packet);
      var error =
          ErrorPacket.builder()
              .code(BizCodes.C400.getCode())
              .message("未找到 [@ns=" + packet.getNs() + "] 处理器")
              .details(packet.toString())
              .build();
      return session.sendPacket(error);
    }
    log.debug("{} 发送消息:{}{}", session, System.lineSeparator(), packet);
    return handler.handle(session, packet);
  }

  // 在 Guice Inject 查询符合要求的消息处理器
  private static Map<String, PacketHandler> findPacketHandlers(Injector injector) {
    var map = new LinkedHashMap<String, PacketHandler>();
    var bindings = injector.findBindingsByType(TypeLiteral.get(PacketHandler.class));
    for (Binding<PacketHandler> b : bindings) {
      var k = b.getKey();
      Named named = (Named) k.getAnnotation();
      var v = b.getProvider().get();
      map.put(named.value(), v);
    }
    return map;
  }
}
