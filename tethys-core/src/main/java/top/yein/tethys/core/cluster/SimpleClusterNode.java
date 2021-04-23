package top.yein.tethys.core.cluster;

import io.grpc.ManagedChannel;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Setter;
import top.yein.tethys.cluster.ClusterNode;
import top.yein.tethys.grpc.HealthGrpc.HealthStub;
import top.yein.tethys.grpc.ServerInfoGrpc.ServerInfoStub;

/** @author KK (kzou227@qq.com) */
public class SimpleClusterNode implements ClusterNode, GrpcStubProvider {

  private int fid;
  private String target;

  // ===================
  private final ManagedChannel channel;
  @Setter private ServerInfoStub serverInfoStub;
  @Setter private HealthStub healthStub;

  /** */
  private AtomicInteger checkFailedCount = new AtomicInteger(0);

  /**
   * @param fid
   * @param target
   * @param channel
   */
  public SimpleClusterNode(int fid, String target, ManagedChannel channel) {
    this.fid = fid;
    this.target = target;
    this.channel = channel;
  }

  @Override
  public String target() {
    return this.target;
  }

  @Override
  public int fid() {
    return fid;
  }

  @Override
  public boolean isAvailable() {
    return checkFailedCount.get() == 0;
  }

  @Override
  public ManagedChannel channel() {
    return channel;
  }

  @Override
  public ServerInfoStub serverInfoStub() {
    return serverInfoStub;
  }

  @Override
  public HealthStub healthStub() {
    return healthStub;
  }

  void incrementCheckFailedCount() {
    checkFailedCount.incrementAndGet();
  }

  void resetCheckFailedCount() {
    checkFailedCount.set(0);
  }
}
