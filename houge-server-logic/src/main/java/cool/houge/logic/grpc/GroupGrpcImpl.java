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
import io.grpc.stub.StreamObserver;

/**
 * 群组 gRPC 服务实现类.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupGrpcImpl extends GroupGrpc.GroupImplBase {

  @Override
  public void create(
      CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
    super.create(request, responseObserver);
  }

  @Override
  public void delete(DeleteGroupRequest request, StreamObserver<Empty> responseObserver) {
    super.delete(request, responseObserver);
  }
}
