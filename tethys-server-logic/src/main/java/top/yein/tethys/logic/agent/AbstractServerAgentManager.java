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

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import top.yein.tethys.grpc.AgentPb;

/**
 * gRPC消息观察者管理器实现.
 *
 * @author KK (kzou227@qq.com)
 */
public abstract class AbstractServerAgentManager implements ServerAgentManager {

  private static final Logger log = LogManager.getLogger();
  private final ConcurrentLinkedQueue<LinkResponseHolder> observerQueue =
      new ConcurrentLinkedQueue<>();

  @Override
  public void register(
      AgentPb.LinkRequest request, ServerCallStreamObserver<AgentPb.LinkResponse> observer) {
    var bean = new LinkResponseHolder(request.getName(), request.getHostName(), observer);
    observerQueue.add(bean);
    log.info("注册消息观察者 name={} hostName={}", bean.name, bean.hostName);

    observer.setOnCancelHandler(
        () -> {
          removeObserver(bean);
          log.info("消息观察者由发起方手动取消 observerName={} hostName={}", bean.name, bean.hostName);
        });
  }

  /**
   * 移除消息观察者.
   *
   * @param holder
   */
  void removeObserver(LinkResponseHolder holder) {
    observerQueue.remove(holder);
  }

  @Override
  public Flux<StreamObserver<AgentPb.LinkResponse>> fetchAgentObservers() {
    return Flux.fromIterable(observerQueue)
        .filter(
            bean -> {
              if (bean.observer.isCancelled()) {
                removeObserver(bean);
              }
              return !bean.observer.isCancelled();
            })
        .map(bean -> bean.observer);
  }
}
