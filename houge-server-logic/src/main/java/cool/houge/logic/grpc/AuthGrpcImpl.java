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

import cool.houge.auth.AuthService;
import cool.houge.grpc.AuthGrpc;
import cool.houge.grpc.AuthPb.AuthRequest;
import cool.houge.grpc.AuthPb.AuthResponse;
import io.grpc.stub.StreamObserver;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 用户认证gRPC服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class AuthGrpcImpl extends AuthGrpc.AuthImplBase {

  private static final Logger log = LogManager.getLogger();
  private final AuthService authService;

  /**
   * 使用认证服务构造对象.
   *
   * @param authService 认证服务
   */
  @Inject
  public AuthGrpcImpl(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public void auth(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
    Mono.defer(() -> authService.authenticate(request.getToken()))
        .map(ac -> AuthResponse.newBuilder().setUid(ac.uid()).build())
        .subscribeOn(Schedulers.parallel())
        .subscribe(new SingleGrpcSubscriber<>(responseObserver));
  }
}
