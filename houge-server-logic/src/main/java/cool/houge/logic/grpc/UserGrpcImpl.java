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

import com.google.common.base.Strings;
import cool.houge.grpc.UserGrpc;
import cool.houge.grpc.UserPb.CreateUserRequest;
import cool.houge.grpc.UserPb.CreateUserResponse;
import cool.houge.service.UserService;
import cool.houge.service.UserService.Create;
import io.grpc.stub.StreamObserver;
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
    var createBuilder = Create.builder();
    if (request.getUid() > 0) {
      createBuilder.uid(request.getUid());
    }
    if (!Strings.isNullOrEmpty(request.getOriginUid())) {
      createBuilder.originUid(request.getOriginUid());
    }

    userService
        .create(createBuilder.build())
        .map(dto -> CreateUserResponse.newBuilder().setUid(dto.getUid()).build())
        .subscribe(new SingleGrpcSubscriber<>(responseObserver));
  }
}
