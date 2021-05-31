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

import com.google.protobuf.Empty;
import cool.houge.grpc.GroupGrpc;
import cool.houge.grpc.GroupPb.CreateGroupRequest;
import cool.houge.grpc.GroupPb.CreateGroupResponse;
import cool.houge.grpc.GroupPb.DeleteGroupRequest;
import cool.houge.grpc.GroupPb.DeleteMemberGroupRequest;
import cool.houge.grpc.GroupPb.JoinMemberGroupRequest;
import cool.houge.service.group.CreateGroupInput;
import cool.houge.service.group.GroupService;
import cool.houge.service.group.JoinMemberInput;
import io.grpc.stub.StreamObserver;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

/**
 * 群组 gRPC 服务实现类.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupGrpcImpl extends GroupGrpc.GroupImplBase {

  private static final Logger log = LogManager.getLogger();
  private final GroupService groupService;

  /**
   * 使用群组服务对象构造对象.
   *
   * @param groupService 群组服务对象
   */
  @Inject
  public GroupGrpcImpl(GroupService groupService) {
    this.groupService = groupService;
  }

  @Override
  public void create(
      CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
    Mono.defer(
            () -> {
              log.debug("创建群组 {}", request);
              var builder = CreateGroupInput.builder();
              if (request.getGid() > 0) {
                builder.gid(request.getGid());
              }
              builder.creatorId(request.getCreatorId()).name(request.getName());
              return groupService
                  .create(builder.build())
                  .map(dto -> CreateGroupResponse.newBuilder().setGid(dto.getGid()).build());
            })
        .subscribe(new SingleGrpcSubscriber<>(responseObserver));
  }

  @Override
  public void delete(DeleteGroupRequest request, StreamObserver<Empty> responseObserver) {
    Mono.defer(
            () -> {
              log.debug("删除群组 {}", request);
              return groupService
                  .delete(request.getGid())
                  .map(unused -> Empty.getDefaultInstance());
            })
        .subscribe(new SingleGrpcSubscriber<>(responseObserver));
  }

  @Override
  public void joinMember(JoinMemberGroupRequest request, StreamObserver<Empty> responseObserver) {
    Mono.defer(
            () -> {
              log.debug("群组加入成员 {}", request);
              var bean =
                  JoinMemberInput.builder().gid(request.getGid()).uid(request.getUid()).build();
              return groupService.joinMember(bean).map(unused -> Empty.getDefaultInstance());
            })
        .subscribe(new SingleGrpcSubscriber<>(responseObserver));
  }

  @Override
  public void deleteMember(
      DeleteMemberGroupRequest request, StreamObserver<Empty> responseObserver) {
    Mono.defer(
            () -> {
              log.debug("群组删除成员 {}", request);
              var bean =
                  JoinMemberInput.builder().gid(request.getGid()).uid(request.getUid()).build();
              return groupService.deleteMember(bean).map(unused -> Empty.getDefaultInstance());
            })
        .subscribe(new SingleGrpcSubscriber<>(responseObserver));
  }
}
