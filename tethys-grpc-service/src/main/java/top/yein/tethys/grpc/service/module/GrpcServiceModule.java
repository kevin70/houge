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
package top.yein.tethys.grpc.service.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import io.grpc.BindableService;
import top.yein.tethys.grpc.HealthGrpc;
import top.yein.tethys.grpc.MessageServiceGrpc;
import top.yein.tethys.grpc.ServerInfoGrpc;
import top.yein.tethys.grpc.service.HealthGrpcImpl;
import top.yein.tethys.grpc.service.MessageServiceGrpcImpl;
import top.yein.tethys.grpc.service.ServerInfoGrpcImpl;

/**
 * Tethys gRPC 服务模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class GrpcServiceModule extends AbstractModule {

  @Override
  protected void configure() {
    // 健康检查
    bind(BindableService.class)
        .annotatedWith(Names.named(HealthGrpc.SERVICE_NAME))
        .to(HealthGrpcImpl.class)
        .in(Scopes.SINGLETON);

    // 服务信息
    bind(BindableService.class)
        .annotatedWith(Names.named(ServerInfoGrpc.SERVICE_NAME))
        .to(ServerInfoGrpcImpl.class)
        .in(Scopes.SINGLETON);

    // 消息服务
    bind(BindableService.class)
        .annotatedWith(Names.named(MessageServiceGrpc.SERVICE_NAME))
        .to(MessageServiceGrpcImpl.class)
        .in(Scopes.SINGLETON);
  }
}
