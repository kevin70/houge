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
package top.yein.tethys.service.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.typesafe.config.Config;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.grpc.MessageServiceGrpc;
import top.yein.tethys.service.RemoteMessageService;
import top.yein.tethys.service.RemoteMessageServiceImpl;

/**
 * gRPC 存根模块.
 *
 * @author KK (kzou227@qq.com)
 */
class GrpcStubModule extends AbstractModule {

  private final Config config;

  GrpcStubModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(RemoteMessageService.class).to(RemoteMessageServiceImpl.class).in(Scopes.SINGLETON);
  }

  @Provides
  public ManagedChannel managedChannel() {
    return ManagedChannelBuilder.forTarget(config.getString(ConfigKeys.GRPC_CHANNEL_TARGET))
        .enableRetry()
        .usePlaintext()
        .build();
  }

  @Provides
  public MessageServiceGrpc.MessageServiceStub messageServiceStub(ManagedChannel channel) {
    return MessageServiceGrpc.newStub(channel);
  }
}
