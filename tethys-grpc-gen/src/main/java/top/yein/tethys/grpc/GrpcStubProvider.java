package top.yein.tethys.grpc;

import io.grpc.ManagedChannel;

/**
 * gRPC 存根对象提供者.
 *
 * @author KK (kzou227@qq.com)
 */
public interface GrpcStubProvider {

  /**
   * 返回 gRPC 连接通道对象.
   *
   * @return gRPC 连接通道
   */
  ManagedChannel channel();

  /**
   * 返回服务健康状况 gRPC 存根对象.
   *
   * @return gRPC 存根对象
   */
  default HealthGrpc.HealthStub healthStub() {
    throw new UnsupportedOperationException("No HealthGrpc.HealthStub");
  }

  /**
   * 返回消息服务 gRPC 存根对象.
   *
   * @return gRPC 存根对象
   */
  default MessageGrpc.MessageStub messageStub() {
    throw new UnsupportedOperationException("No MessageGrpc.MessageStub");
  }
}
