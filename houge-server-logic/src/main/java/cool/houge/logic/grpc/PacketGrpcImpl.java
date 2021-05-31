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
package cool.houge.logic.grpc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.google.common.base.Strings;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.protobuf.ByteString;
import cool.houge.grpc.PacketGrpc;
import cool.houge.grpc.PacketPb.PacketRequest;
import cool.houge.grpc.PacketPb.PacketResponse;
import cool.houge.logic.handler.PacketHandler;
import cool.houge.logic.packet.ErrorPacket;
import cool.houge.logic.packet.MessagePacketBase;
import cool.houge.logic.packet.Packet;
import cool.houge.util.JsonUtils;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;

/**
 * 消息包 gRPC 实现类.
 *
 * @author KK (kzou227@qq.com)
 */
public class PacketGrpcImpl extends PacketGrpc.PacketImplBase {

  private static final Logger log = LogManager.getLogger();
  private final ObjectReader packetReader;
  private final ObjectWriter packetWriter;
  private final Map<String, PacketHandler<Packet>> packetHandlers;

  /**
   * 使用 Guice Injector 构建对象.
   *
   * @param injector Guice Injector
   */
  @Inject
  public PacketGrpcImpl(@Nonnull Injector injector) {
    this.packetReader = JsonUtils.objectMapper().readerFor(Packet.class);
    this.packetWriter = JsonUtils.objectMapper().writerFor(Packet.class);
    this.packetHandlers = findPacketHandlers(injector);
  }

  @Override
  public void process(PacketRequest request, StreamObserver<PacketResponse> responseObserver) {
    // 解析-消息包
    Packet packet;
    try {
      packet = packetReader.readValue(request.getDataBytes().newInput());
    } catch (InvalidTypeIdException e) {
      var ep =
          ErrorPacket.builder()
              .code(BizCode.C0.getCode())
              .message(Strings.lenientFormat("未定义的消息类型[@ns=%s]", e.getTypeId()))
              .build();
      handleErrorPacket(responseObserver, ep);
      return;
    } catch (JsonParseException e) {
      // JSON格式错误
      log.info("JSON解析格式错误 requestUid={}", request.getRequestUid(), e);
      var ep = ErrorPacket.builder().code(BizCode.C0.getCode()).message(e.getMessage()).build();
      handleErrorPacket(responseObserver, ep);
      return;
    } catch (IOException e) {
      log.error("解析Packet请求出现IO错误 requestUid={}", request.getRequestUid(), e);
      var ep = ErrorPacket.builder().code(BizCode.C0.getCode()).message(e.getMessage()).build();
      handleErrorPacket(responseObserver, ep);
      return;
    }

    log.debug("收到Packet请求 requestUid={} packet={}", request.getRequestUid(), packet);
    var handler = packetHandlers.get(packet.getNs());
    if (handler == null) {
      log.error("未找到PacketHandler requestUid={} ns={}", request.getRequestUid(), packet.getNs());
      var ep =
          ErrorPacket.builder()
              .code(BizCode.C0.getCode())
              .message(Strings.lenientFormat("未找到消息类型[@ns=%s]的实现", packet.getNs()))
              .build();
      handleErrorPacket(responseObserver, ep);
      return;
    }

    if (packet instanceof MessagePacketBase) {
      var mp = (MessagePacketBase) packet;
      // 消息包的 from 值为空时默认设置为发送用户 ID
      if (mp.getFrom() == null) {
        mp.setFrom(request.getRequestUid());
      }
    }

    // 处理消息Packet
    Mono.defer(() -> handler.handle(packet))
        .subscribeOn(Schedulers.parallel())
        .subscribe(
            unused -> {
              responseObserver.onNext(PacketResponse.getDefaultInstance());
              responseObserver.onCompleted();
            },
            t -> handleThrowable(responseObserver, t));
  }

  private void handleThrowable(StreamObserver<PacketResponse> responseObserver, Throwable t) {
    if (t instanceof BizCodeException) {
      var ex = (BizCodeException) t;
      var bizCode = ex.getBizCode();
      if (bizCode.getGrpcStatus() >= 0) {
        log.error("{}", t);
        var status = Status.fromCodeValue(bizCode.getGrpcStatus());
        responseObserver.onError(status.asRuntimeException());
      } else {
        var ep = ErrorPacket.builder().code(bizCode.getCode()).message(ex.getRawMessage()).build();
        handleErrorPacket(responseObserver, ep);
      }
    } else {
      log.error("{}", t);
      responseObserver.onError(t);
    }
  }

  private void handleErrorPacket(
      StreamObserver<PacketResponse> responseObserver, ErrorPacket packet) {
    var output = ByteString.newOutput();
    try {
      packetWriter.writeValue(output, packet);
    } catch (IOException e) {
      log.error("序列化 ErrorPacket JSON 错误", e);
      responseObserver.onError(e);
      return;
    }
    var response = PacketResponse.newBuilder().setDataBytes(output.toByteString()).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  // 在 Guice Inject 查询符合要求的消息处理器
  private static Map<String, PacketHandler<Packet>> findPacketHandlers(Injector injector) {
    var map = new LinkedHashMap<String, PacketHandler<Packet>>();
    var bindings = injector.findBindingsByType(TypeLiteral.get(PacketHandler.class));
    for (Binding<? extends PacketHandler> b : bindings) {
      var k = b.getKey();
      var named = (Named) k.getAnnotation();
      var v = b.getProvider().get();
      map.put(named.value(), v);
    }
    return map;
  }
}
