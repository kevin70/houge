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
package top.yein.tethys.logic.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import io.grpc.BindableService;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.auth.impl.JwsAuthService;
import top.yein.tethys.grpc.AgentGrpc;
import top.yein.tethys.grpc.AuthGrpc;
import top.yein.tethys.grpc.PacketGrpc;
import top.yein.tethys.grpc.UserGroupGrpc;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.id.YeinGidMessageIdGenerator;
import top.yein.tethys.logic.agent.PacketSender;
import top.yein.tethys.logic.agent.ServerAgentManager;
import top.yein.tethys.logic.agent.TediousServerAgentManager;
import top.yein.tethys.logic.grpc.AgentGrpcImpl;
import top.yein.tethys.logic.grpc.AuthGrpcImpl;
import top.yein.tethys.logic.grpc.PacketGrpcImpl;
import top.yein.tethys.logic.grpc.UserGroupGrpcImpl;
import top.yein.tethys.logic.handler.GroupMessageHandler;
import top.yein.tethys.logic.handler.PacketHandler;
import top.yein.tethys.logic.handler.PrivateMessageHandler;
import top.yein.tethys.logic.packet.Packet;
import top.yein.tethys.logic.support.LogicApplicationIdentifier;
import top.yein.tethys.system.identifier.ApplicationIdentifier;

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
    bindGrpcImpl(UserGroupGrpcImpl.class, UserGroupGrpc.SERVICE_NAME);
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
