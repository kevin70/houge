package top.yein.tethys.im.cluster;

import io.grpc.ManagedChannel;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Setter;
import top.yein.tethys.cluster.ClusterNode;
import top.yein.tethys.grpc.GrpcStubProvider;
import top.yein.tethys.grpc.HealthGrpc.HealthStub;
import top.yein.tethys.grpc.MessageGrpc.MessageStub;

/** @author KK (kzou227@qq.com) */
public class SimpleClusterNode implements ClusterNode, GrpcStubProvider {

  private String target;

  // ===================
  private final ManagedChannel channel;
  @Setter private HealthStub healthStub;
  @Setter private MessageStub messageStub;

  /** */
  private AtomicInteger checkFailedCount = new AtomicInteger(0);

  /**
   * @param target
   * @param channel
   */
  public SimpleClusterNode(String target, ManagedChannel channel) {
    this.target = target;
    this.channel = channel;
  }

  @Override
  public String target() {
    return this.target;
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
  public HealthStub healthStub() {
    return healthStub;
  }

  @Override
  public MessageStub messageStub() {
    return messageStub;
  }

  void incrementCheckFailedCount() {
    checkFailedCount.incrementAndGet();
  }

  void resetCheckFailedCount() {
    checkFailedCount.set(0);
  }
}
