package top.yein.tethys.core.cluster;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.cluster.ClusterNode;
import top.yein.tethys.cluster.ClusterNodesQuery;
import top.yein.tethys.core.util.MonoSinkStreamObserver;
import top.yein.tethys.grpc.HealthCheckRequest;
import top.yein.tethys.grpc.HealthCheckResponse;
import top.yein.tethys.grpc.HealthCheckResponse.ServingStatus;
import top.yein.tethys.grpc.HealthGrpc;
import top.yein.tethys.grpc.ServerInfoGrpc;
import top.yein.tethys.grpc.ServerInfoRequest;
import top.yein.tethys.grpc.ServerInfoResponse;

/**
 * 简单的集群管理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class SimpleClusterManager implements ClusterNodesQuery, AutoCloseable {

  private final Duration CHECK_HEALTH_DURATION = Duration.ofSeconds(30);
  private AtomicBoolean RUNNING = new AtomicBoolean(true);

  private final ApplicationIdentifier applicationIdentifier;
  private List<SimpleClusterNode> clusterNodes;

  /**
   * @param grpcTargets
   * @param applicationIdentifier
   */
  public SimpleClusterManager(String grpcTargets, ApplicationIdentifier applicationIdentifier) {
    this.applicationIdentifier = applicationIdentifier;
    if (Strings.isNullOrEmpty(grpcTargets)) {
      throw new IllegalArgumentException("集群[grpc.targets]配置不能为空");
    }

    this.clusterNodes = this.loadClusterNodes(grpcTargets);
    if (this.clusterNodes.isEmpty()) {
      log.warn("集群正在启用中但并没有可用的集群节点 grpcTargets={}", grpcTargets);
    } else {
      this.initCheckHealth();
    }
  }

  @Override
  public List<? extends ClusterNode> queryNodes() {
    return clusterNodes;
  }

  @Override
  public Flux<? extends ClusterNode> queryAvailableNodes() {
    return Flux.fromIterable(queryNodes()).filter(ClusterNode::isAvailable);
  }

  @Override
  public void close() throws Exception {
    if (clusterNodes == null && clusterNodes.isEmpty()) {
      return;
    }
    for (SimpleClusterNode node : clusterNodes) {
      if (node.channel().isShutdown()) {
        log.warn("集群连接通道已经断开 fid={} target={}", node.fid(), node.target());
        return;
      }
      try {
        log.info("正在断开集群连接通道 fid={} target={}", node.fid(), node.target());
        node.channel().shutdownNow();
      } catch (Exception e) {
        log.error("断开集群连接通道错误 fid={} target={}", node.fid(), node.target(), e);
      }
    }
    log.info("集群管理已经完成清理");
  }

  @VisibleForTesting
  List<SimpleClusterNode> loadClusterNodes(String grpcTargets) {
    var targets = grpcTargets.split(",");
    var nodeMap = new HashMap<Integer, SimpleClusterNode>();
    for (String target : targets) {
      var hap = HostAndPort.fromString(target);
      var channel =
          ManagedChannelBuilder.forAddress(hap.getHost(), hap.getPort()).usePlaintext().build();
      var infoResponse = getServerInfo(channel);
      if (infoResponse.getFid() == applicationIdentifier.fid()) {
        try {
          channel.shutdownNow();
        } catch (RuntimeException e) {
          // ignore
        }
        log.debug("已过滤集群配置 target={} 与当前实例相同 fid={}", target, applicationIdentifier.fid());
        continue;
      }

      // 存在相同的集群节点配置
      if (nodeMap.containsKey(infoResponse.getFid())) {
        try {
          channel.shutdownNow();
        } catch (RuntimeException e) {
          // ignore
        }
        log.warn(
            "已自动过滤[{}]集群节点配置因为与[{}]指向同一个实例 fid={}",
            target,
            nodeMap.get(infoResponse.getFid()).target(),
            infoResponse.getFid());
        continue;
      }

      var node = new SimpleClusterNode(infoResponse.getFid(), target, channel);
      // 初始化 gRPC 存根
      node.setServerInfoStub(ServerInfoGrpc.newStub(channel));
      node.setHealthStub(HealthGrpc.newStub(channel));
      nodeMap.put(infoResponse.getFid(), node);
    }
    return ImmutableList.copyOf(nodeMap.values());
  }

  @VisibleForTesting
  ServerInfoResponse getServerInfo(ManagedChannel channel) {
    var stub = ServerInfoGrpc.newBlockingStub(channel);
    var request = ServerInfoRequest.newBuilder().build();
    return stub.getServerInfo(request);
  }

  @VisibleForTesting
  void initCheckHealth() {
    Flux.defer(() -> Flux.fromIterable(clusterNodes))
        .doOnNext(this::checkHealth0)
        .repeat(RUNNING::get)
        .delaySubscription(CHECK_HEALTH_DURATION)
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
  }

  private void checkHealth0(SimpleClusterNode node) {
    Mono.<HealthCheckResponse>create(
            sink -> {
              var healthStub = node.healthStub();
              var request = HealthCheckRequest.newBuilder().setService("Hello World").build();
              healthStub.check(request, new MonoSinkStreamObserver<>(sink));
            })
        .subscribe(
            response -> {
              if (response.getStatus() == ServingStatus.SERVING) {
                if (!node.isAvailable()) {
                  node.resetCheckFailedCount();
                }
              } else {
                node.incrementCheckFailedCount();
                log.error(
                    "集群节点健康检查状态异常[status={}] fid={} target={}",
                    response.getStatus(),
                    node.fid(),
                    node.target());
              }
            },
            ex -> {
              node.incrementCheckFailedCount();
              log.error("集群节点健康检查异常 fid={} target={}", node.fid(), node.target(), ex);
            });
  }
}
