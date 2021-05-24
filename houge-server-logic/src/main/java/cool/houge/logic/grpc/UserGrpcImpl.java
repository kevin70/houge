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

import cool.houge.grpc.UserGrpc;
import cool.houge.grpc.UserPb.CreateUserRequest;
import cool.houge.grpc.UserPb.CreateUserResponse;
import cool.houge.service.UserService;
import cool.houge.service.vo.CreateUserVO;
import io.grpc.stub.StreamObserver;
import java.util.Objects;
import javax.inject.Inject;

/**
 * 用户 gRPC 服务实现类.
 *
 * @author KK (kzou227@qq.com)
 */
public class UserGrpcImpl extends UserGrpc.UserImplBase {

  private final UserService userService;

  /**
   * 使用用户服务构建对象.
   *
   * @param userService 用户服务对象
   */
  @Inject
  public UserGrpcImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void create(
      CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
    var defaultInstance = request.getDefaultInstanceForType();
    var vo = new CreateUserVO();
    if (request.getId() != defaultInstance.getId()) {
      vo.setId(request.getId());
    }
    if (request.getOriginUid() != defaultInstance.getOriginUid()) {
      vo.setOriginUid(request.getOriginUid());
    }

    userService
        .create(vo)
        .map(
            dto -> {
              var builder = CreateUserResponse.newBuilder();
              if (!Objects.equals(request.getId(), dto.getId())) {
                builder.setId(dto.getId());
              }
              return builder.build();
            })
        .subscribe(new SingleGrpcSubscriber<>(responseObserver));
  }
}
