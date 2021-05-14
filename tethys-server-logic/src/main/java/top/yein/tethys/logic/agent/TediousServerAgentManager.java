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

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import top.yein.tethys.grpc.AgentPb;
import top.yein.tethys.grpc.AgentPb.PacketMixin;
import top.yein.tethys.grpc.AgentPb.PacketMixinType;
import top.yein.tethys.logic.packet.Packet;
import top.yein.tethys.util.JsonUtils;

/**
 * 向所有观察者分发消息.
 *
 * @author KK (kzou227@qq.com)
 */
public class TediousServerAgentManager extends AbstractServerAgentManager
    implements CommandSender, PacketSender {

  private static final Logger log = LogManager.getLogger();
  private final ObjectWriter objectWriter;

  /** 默认构造函数. */
  public TediousServerAgentManager() {
    this.objectWriter = JsonUtils.objectMapper().writerFor(Packet.class);
  }

  @Override
  public void send(AgentPb.Command command) {
    // 分发命令
    fetchAgentObservers()
        .subscribe(
            observer -> {
              log.debug("发送Command {} {}", command);
              observer.onNext(AgentPb.LinkResponse.newBuilder().setCommand(command).build());
            });
  }

  @Override
  public void sendToUser(List<Long> uids, Packet packet) {
    send(uids, packet, PacketMixinType.USER);
  }

  @Override
  public void sendToGroup(List<Long> gids, Packet packet) {
    send(gids, packet, PacketMixinType.GROUP);
  }

  @Override
  public void sendToAll(Packet packet) {
    send(List.of(), packet, AgentPb.PacketMixinType.ALL);
  }

  private void send(List<Long> list, Packet packet, AgentPb.PacketMixinType type) {
    AtomicReference<ByteString> dataRef = new AtomicReference<>();
    fetchAgentObservers()
        .onErrorResume(
            ex -> {
              log.error("分发Packet错误 uids={} packet={}", list, packet, ex);
              return Mono.empty();
            })
        .doFirst(() -> dataRef.set(serializePacket(packet)))
        .subscribe(
            observer -> {
              var forwardBuilder =
                  PacketMixin.newBuilder().setType(type).setDataBytes(dataRef.get());
              if (!list.isEmpty()) {
                forwardBuilder.addAllTo(list);
              }
              var response =
                  AgentPb.LinkResponse.newBuilder().setPacketMixin(forwardBuilder).build();
              observer.onNext(response);
            },
            t -> log.error("分发Packet未处理异常 to={} packet={}", list, packet, t));
  }

  private ByteString serializePacket(Packet packet) {
    try {
      // 将Packet序列化JSON数据
      var output = ByteString.newOutput();
      objectWriter.writeValue(output, packet);
      return output.toByteString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
