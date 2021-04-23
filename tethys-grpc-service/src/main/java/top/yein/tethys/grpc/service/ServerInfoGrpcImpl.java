package top.yein.tethys.grpc.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.grpc.ServerInfoGrpc;
import top.yein.tethys.grpc.ServerInfoRequest;
import top.yein.tethys.grpc.ServerInfoResponse;

/** @author KK (kzou227@qq.com) */
@Log4j2
public class ServerInfoGrpcImpl extends ServerInfoGrpc.ServerInfoImplBase {

  private final ApplicationIdentifier applicationIdentifier;

  /** @param applicationIdentifier */
  public ServerInfoGrpcImpl(ApplicationIdentifier applicationIdentifier) {
    this.applicationIdentifier = applicationIdentifier;
  }

  @Override
  public void getServerInfo(
      ServerInfoRequest request, StreamObserver<ServerInfoResponse> responseObserver) {
    log.debug("获取集群节点信息 > fid={}", request.getFid());
    var resp =
        ServerInfoResponse.newBuilder()
            .setFid(applicationIdentifier.fid())
            .setName(applicationIdentifier.applicationName())
            .build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
}
