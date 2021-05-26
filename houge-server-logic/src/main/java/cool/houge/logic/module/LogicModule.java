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
package cool.houge.logic.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import cool.houge.auth.AuthService;
import cool.houge.auth.impl.JwsAuthService;
import cool.houge.grpc.AgentGrpc;
import cool.houge.grpc.AuthGrpc;
import cool.houge.grpc.GroupGrpc;
import cool.houge.grpc.MessageGrpc;
import cool.houge.grpc.PacketGrpc;
import cool.houge.grpc.UserGroupGrpc;
import cool.houge.grpc.UserGrpc;
import cool.houge.id.MessageIdGenerator;
import cool.houge.id.YeinGidMessageIdGenerator;
import cool.houge.logic.agent.PacketSender;
import cool.houge.logic.agent.ServerAgentManager;
import cool.houge.logic.agent.TediousServerAgentManager;
import cool.houge.logic.grpc.AgentGrpcImpl;
import cool.houge.logic.grpc.AuthGrpcImpl;
import cool.houge.logic.grpc.GroupGrpcImpl;
import cool.houge.logic.grpc.MessageGrpcImpl;
import cool.houge.logic.grpc.PacketGrpcImpl;
import cool.houge.logic.grpc.UserGroupGrpcImpl;
import cool.houge.logic.grpc.UserGrpcImpl;
import cool.houge.logic.handler.GroupMessageHandler;
import cool.houge.logic.handler.PacketHandler;
import cool.houge.logic.handler.PrivateMessageHandler;
import cool.houge.logic.packet.Packet;
import cool.houge.logic.support.LogicApplicationIdentifier;
import cool.houge.system.identifier.ApplicationIdentifier;
import io.grpc.BindableService;

/**
 * LogicGuice模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class LogicModule extends AbstractModule {

  @Override
  protected void configure() {
    bindGrpcServices();
    bindPacketHandler();

    // 消息分发器
    bind(TediousServerAgentManager.class).in(Scopes.SINGLETON);
    bind(PacketSender.class).to(TediousServerAgentManager.class);
    bind(ServerAgentManager.class).to(TediousServerAgentManager.class);

    bind(ApplicationIdentifier.class).to(LogicApplicationIdentifier.class).in(Scopes.SINGLETON);
    bind(MessageIdGenerator.class).to(YeinGidMessageIdGenerator.class).in(Scopes.SINGLETON);

    // 认证服务
    bind(JwsAuthService.class).in(Scopes.SINGLETON);
    bind(AuthService.class).to(JwsAuthService.class);
  }

  private void bindGrpcServices() {
    bindGrpcImpl(AgentGrpcImpl.class, AgentGrpc.SERVICE_NAME);
    bindGrpcImpl(AuthGrpcImpl.class, AuthGrpc.SERVICE_NAME);
    bindGrpcImpl(PacketGrpcImpl.class, PacketGrpc.SERVICE_NAME);
    bindGrpcImpl(UserGrpcImpl.class, UserGrpc.SERVICE_NAME);
    bindGrpcImpl(GroupGrpcImpl.class, GroupGrpc.SERVICE_NAME);
    bindGrpcImpl(UserGroupGrpcImpl.class, UserGroupGrpc.SERVICE_NAME);
    bindGrpcImpl(MessageGrpcImpl.class, MessageGrpc.SERVICE_NAME);
  }

  private void bindPacketHandler() {
    // PacketHandlers =========================================>>>
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Packet.NS_PRIVATE_MESSAGE))
        .to(PrivateMessageHandler.class)
        .in(Scopes.SINGLETON);
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Packet.NS_GROUP_MESSAGE))
        .to(GroupMessageHandler.class)
        .in(Scopes.SINGLETON);
    // PacketHandlers =========================================<<<
  }

  private void bindGrpcImpl(Class<? extends BindableService> clazz, String serviceName) {
    bind(BindableService.class)
        .annotatedWith(Names.named(serviceName))
        .to(clazz)
        .in(Scopes.SINGLETON);
  }
}
