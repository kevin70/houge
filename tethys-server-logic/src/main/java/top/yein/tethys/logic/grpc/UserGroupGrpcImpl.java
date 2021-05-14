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

import io.grpc.stub.StreamObserver;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import top.yein.tethys.grpc.UserGroupPb.ListGidsRequest;
import top.yein.tethys.grpc.UserGroupPb.ListGidsResponse;
import top.yein.tethys.grpc.UserGroupGrpc;
import top.yein.tethys.storage.query.GroupQueryDao;

/**
 * 用户群组gRPC服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class UserGroupGrpcImpl extends UserGroupGrpc.UserGroupImplBase {

  private static final Logger log = LogManager.getLogger();
  private final GroupQueryDao groupQueryDao;

  /** @param groupQueryDao */
  @Inject
  public UserGroupGrpcImpl(GroupQueryDao groupQueryDao) {
    this.groupQueryDao = groupQueryDao;
  }

  @Override
  public void listGids(ListGidsRequest request, StreamObserver<ListGidsResponse> responseObserver) {
    Flux.defer(() -> groupQueryDao.queryGidByUid(request.getUid()))
        .subscribeOn(Schedulers.parallel())
        .collectList()
        .subscribe(
            gids -> {
              responseObserver.onNext(ListGidsResponse.newBuilder().addAllGid(gids).build());
              responseObserver.onCompleted();
            },
            ex -> {
              log.error("查询用户[{}]的群组IDs错误", request.getUid(), ex);
              responseObserver.onError(ex);
            });
  }
}
