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
package top.yein.tethys.logic.grpc;

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
import top.yein.tethys.grpc.PacketGrpc;
import top.yein.tethys.grpc.PacketPb.PacketRequest;
import top.yein.tethys.grpc.PacketPb.PacketResponse;
import top.yein.tethys.logic.handler.PacketHandler;
import top.yein.tethys.logic.handler.Result;
import top.yein.tethys.logic.packet.Packet;
import top.yein.tethys.util.JsonUtils;

/**
 * 消息包 gRPC 实现类.
 *
 * @author KK (kzou227@qq.com)
 */
public class PacketGrpcImpl extends PacketGrpc.PacketImplBase {

  private static final Logger log = LogManager.getLogger();
  private final ObjectReader packetReader;
  private final ObjectWriter resultWriter;
  private final Map<String, PacketHandler<Packet>> packetHandlers;

  /**
   * 使用 Guice Injector 构建对象.
   *
   * @param injector Guice Injector
   */
  @Inject
  public PacketGrpcImpl(@Nonnull Injector injector) {
    this.packetReader = JsonUtils.objectMapper().readerFor(Packet.class);
    this.resultWriter = JsonUtils.objectMapper().writerFor(Result.class);
    this.packetHandlers = findPacketHandlers(injector);
  }

  @Override
  public void process(PacketRequest request, StreamObserver<PacketResponse> responseObserver) {
    // 解析-消息包
    Packet packet;
    try {
      packet = packetReader.readValue(request.getDataBytes().newInput());
    } catch (InvalidTypeIdException e) {
      handleFailedData(
          responseObserver,
          Strings.lenientFormat(
              "{\"@ns\":\"error\",\"message\":\"未定义的消息类型[@ns=%s]\"}", e.getTypeId()));
      return;
    } catch (JsonParseException e) {
      // JSON格式错误
      log.info("JSON解析格式错误 requestUid={}", request.getRequestUid(), e);
      handleFailedData(
          responseObserver,
          Strings.lenientFormat("{\"@ns\":\"error\",\"message\":\"%s\"}", e.getMessage()));
      return;
    } catch (IOException e) {
      log.error("解析Packet请求出现IO错误 requestUid={}", request.getRequestUid(), e);
      handleFailedData(
          responseObserver,
          Strings.lenientFormat("{\"@ns\":\"error\",\"message\":\"%s\"}", e.getMessage()));
      return;
    }

    log.debug("收到Packet请求 requestUid={} packet={}", request.getRequestUid(), packet);
    var handler = packetHandlers.get(packet.getNs());
    if (handler == null) {
      log.error("未找到PacketHandler requestUid={} ns={}", request.getRequestUid(), packet.getNs());
      handleFailedData(
          responseObserver,
          Strings.lenientFormat(
              "{\"@ns\":\"error\",\"message\":\"未找到消息类型[@ns=%s]的实现\"}", packet.getNs()));
      return;
    }

    // 处理消息Packet
    Mono.defer(() -> handler.handle(request.getRequestUid(), packet))
        .subscribeOn(Schedulers.parallel())
        .subscribe(
            r -> handleResult(responseObserver, r), t -> handleThrowable(responseObserver, t));
  }

  private void handleResult(StreamObserver<PacketResponse> responseObserver, Result result) {
    var builder = PacketResponse.newBuilder().setSuccess(result.isError());
    if (result.getOk() != null || result.getError() != null) {
      // TIPS: 后期需要研究确认是否可以使用 Direct ByteBuffer 进行优化
      var out = ByteString.newOutput();
      try {
        resultWriter.writeValue(out, result);
        builder.setDataBytes(out.toByteString());
      } catch (IOException e) {
        log.error("Result响应结果JSON序列化错误", result, e);
      }
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  private void handleThrowable(StreamObserver<PacketResponse> responseObserver, Throwable t) {
    Result result;
    if (t instanceof BizCodeException) {
      var ex = (BizCodeException) t;
      result = Result.error(ex.getBizCode().getCode(), ex.getMessage(), ex.getContextEntries());
      // FIXME 处理异常
      log.debug("", t);
    } else {
      log.error("未映射的异常", t);
      result = Result.error(BizCode.C0.getCode(), t.getMessage());
    }

    var output = ByteString.newOutput();
    try {
      resultWriter.writeValue(output, result);
    } catch (IOException e) {
      log.error("序列化RESULT JSON错误", e);
      responseObserver.onError(e);
      return;
    }

    var response =
        PacketResponse.newBuilder().setSuccess(false).setDataBytes(output.toByteString()).build();
    responseObserver.onNext(response);
  }

  private void handleFailedData(StreamObserver<PacketResponse> responseObserver, String data) {
    handleFailedData(responseObserver, ByteString.copyFromUtf8(data));
  }

  private void handleFailedData(StreamObserver<PacketResponse> responseObserver, ByteString data) {
    var response = PacketResponse.newBuilder().setSuccess(false).setDataBytes(data).build();
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
