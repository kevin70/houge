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

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import javax.inject.Inject;
import top.yein.tethys.grpc.AgentGrpc;
import top.yein.tethys.grpc.AgentPb.LinkRequest;
import top.yein.tethys.grpc.AgentPb.LinkResponse;
import top.yein.tethys.logic.agent.ServerAgentManager;

/**
 * 命令监控服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class AgentGrpcImpl extends AgentGrpc.AgentImplBase {

  private final ServerAgentManager serverAgentManager;

  /** @param serverAgentManager */
  @Inject
  public AgentGrpcImpl(ServerAgentManager serverAgentManager) {
    this.serverAgentManager = serverAgentManager;
  }

  @Override
  public void link(LinkRequest request, StreamObserver<LinkResponse> responseObserver) {
    serverAgentManager.register(request, (ServerCallStreamObserver<LinkResponse>) responseObserver);
  }
}
