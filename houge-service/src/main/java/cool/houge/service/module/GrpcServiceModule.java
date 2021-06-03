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
package cool.houge.service.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.typesafe.config.Config;
import cool.houge.grpc.MessageGrpc;
import cool.houge.service.message.SendMessageService;
import cool.houge.service.message.impl.SendMessageServiceImpl;
import io.grpc.ManagedChannelBuilder;

/**
 * gRPC 存根模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class GrpcServiceModule extends AbstractModule {

  private final Config config;

  /**
   * 使用应用配置构造对象.
   *
   * @param config 应用配置
   */
  public GrpcServiceModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    var grpcTarget = config.getString("logic-service.grpc-target");
    var channel =
        ManagedChannelBuilder.forTarget(grpcTarget)
            .usePlaintext()
            .enableRetry()
            .disableServiceConfigLookUp()
            .build();
    bind(MessageGrpc.MessageStub.class).toInstance(MessageGrpc.newStub(channel));

    bind(SendMessageService.class).to(SendMessageServiceImpl.class).in(Scopes.SINGLETON);
  }
}
