package top.yein.tethys.grpc.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import top.yein.tethys.grpc.HealthCheckRequest;
import top.yein.tethys.grpc.HealthCheckResponse;
import top.yein.tethys.grpc.HealthCheckResponse.ServingStatus;
import top.yein.tethys.grpc.HealthGrpc;

/**
 * gRPC 健康检查.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class HealthGrpcImpl extends HealthGrpc.HealthImplBase {

  @Override
  public void check(
      HealthCheckRequest request, StreamObserver<HealthCheckResponse> responseObserver) {
    log.debug("gRPC健康检查 fid={}");
    responseObserver.onNext(
        HealthCheckResponse.newBuilder().setStatus(ServingStatus.SERVING).build());
    responseObserver.onCompleted();
  }
}
