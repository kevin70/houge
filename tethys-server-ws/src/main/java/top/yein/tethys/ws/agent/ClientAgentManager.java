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
package top.yein.tethys.ws.agent;

import com.google.common.base.Strings;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.yein.tethys.grpc.AgentGrpc;
import top.yein.tethys.grpc.AgentGrpc.AgentStub;
import top.yein.tethys.grpc.AgentPb;
import top.yein.tethys.ws.AgentServiceConfig;

/**
 * 消息监控管理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class ClientAgentManager {

  private static final Logger log = LogManager.getLogger();
  /** 跳过重复错误日志的限制. */
  private static final int SKIP_REPEAT_ERROR_LOG_LIMIT = 16;

  private final String name;
  private final AgentServiceConfig agentServiceConfig;
  private final PacketProcessor packetProcessor;
  private final CommandProcessor commandProcessor;
  private final AtomicBoolean STARTED = new AtomicBoolean();
  private final AtomicBoolean RUN = new AtomicBoolean(true);
  private final List<ManagedChannel> channels = new CopyOnWriteArrayList<>();

  /**
   * 使用配置及处理器构造对象.
   *
   * @param agentServiceConfig 配置对象
   * @param packetProcessor Packet处理器
   * @param commandProcessor Command处理器
   */
  @Inject
  public ClientAgentManager(
      AgentServiceConfig agentServiceConfig,
      PacketProcessor packetProcessor,
      CommandProcessor commandProcessor) {
    var pid = ProcessHandle.current().pid();
    var ran = ThreadLocalRandom.current().nextInt(1, Short.MAX_VALUE);
    this.name = Strings.lenientFormat("tethys-ws-%s.%s", pid, ran);
    this.agentServiceConfig = agentServiceConfig;
    this.packetProcessor = packetProcessor;
    this.commandProcessor = commandProcessor;
  }

  /** 启动监控管理器. */
  public void start() {
    if (!STARTED.compareAndSet(false, true)) {
      throw new IllegalStateException("已启动的 WatchManager");
    }

    for (String target : agentServiceConfig.getTargets()) {
      var channel =
          ManagedChannelBuilder.forTarget(target)
              .disableServiceConfigLookUp()
              .usePlaintext()
              .disableRetry()
              .build();
      channels.add(channel);
      log.info("初始化监控通道完成 target={} channel={}", target, channel);

      new WatchHelper(channel).watch();
    }
  }

  /** 停止监控管理器. */
  public void stop() {
    if (!RUN.compareAndSet(true, false)) {
      throw new IllegalStateException("已停止的 WatchManager");
    }

    for (ManagedChannel channel : channels) {
      channel.shutdownNow();
    }
  }

  class WatchHelper {

    private final ManagedChannel channel;
    private final AgentStub agentStub;
    private final AtomicInteger retryCount;
    private final AtomicReference<Status.Code> lastStatusCodeRef;

    WatchHelper(ManagedChannel channel) {
      this.channel = channel;
      this.agentStub = AgentGrpc.newStub(channel);
      this.retryCount = new AtomicInteger();
      this.lastStatusCodeRef = new AtomicReference<>();
    }

    void watch() {
      var request =
          AgentPb.LinkRequest.newBuilder().setName(name).setHostName(getHostName()).build();
      log.info("请求监听消息响应 name={} retryCount={} channel={}", name, retryCount.get(), channel);
      this.agentStub.link(request, response());
    }

    private ClientResponseObserver<AgentPb.LinkRequest, AgentPb.LinkResponse> response() {
      return new ClientResponseObserver<>() {

        ClientCallStreamObserver<AgentPb.LinkRequest> requestStream;

        @Override
        public void beforeStart(ClientCallStreamObserver<AgentPb.LinkRequest> requestStream) {
          this.requestStream = requestStream;
        }

        @Override
        public void onNext(AgentPb.LinkResponse response) {
          if (retryCount.get() > 0) {
            retryCount.set(0);
            lastStatusCodeRef.set(Code.OK);
          }

          if (response.hasPacketMixin()) {
            packetProcessor.process(response.getPacketMixin());
          } else if (response.hasCommand()) {
            commandProcessor.process(response.getCommand());
          } else {
            log.error("不支持的 WatchResponse 响应 channel={} response={}", channel, response);
          }
        }

        @Override
        public void onError(Throwable t) {
          requestStream.cancel("watch-error", t);

          Status status = null;
          if (t instanceof StatusRuntimeException) {
            status = ((StatusRuntimeException) t).getStatus();
          } else if (t instanceof StatusException) {
            status = ((StatusException) t).getStatus();
          }
          if (status != null) {
            // 如果最新的响应状态码与上次的一致，则根据错误次数判断是否需要打印日志，减少重复的错误日志打印
            if (status.getCode() != WatchHelper.this.lastStatusCodeRef.get()
                || retryCount.getAndIncrement() == 0) {
              log.error("消息响应监听错误", t);
            }
            WatchHelper.this.lastStatusCodeRef.set(status.getCode());
          } else {
            log.error("未知的消息响应监听异常", t);
          }

          // 最长等待的时长
          var waitSecs = Math.min(retryCount.get() * 10, 60);
          LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(waitSecs));

          // 重试
          watch();
          retryCount.compareAndSet(SKIP_REPEAT_ERROR_LOG_LIMIT, 0);
        }

        @Override
        public void onCompleted() {
          log.info("消息响应监听完成 channel={}", channel);
        }
      };
    }

    private String getHostName() {
      try {
        return InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
        return "UnknownHost";
      }
    }
  }
}
