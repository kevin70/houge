package top.yein.tethys.core.cluster;

import io.grpc.ManagedChannel;
import top.yein.tethys.grpc.HealthGrpc;
import top.yein.tethys.grpc.ServerInfoGrpc;

/** @author KK (kzou227@qq.com) */
public interface GrpcStubProvider {

  /** @return */
  ManagedChannel channel();

  /** @return */
  ServerInfoGrpc.ServerInfoStub serverInfoStub();

  /** @return */
  HealthGrpc.HealthStub healthStub();
}
