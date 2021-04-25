package top.yein.tethys.im.cluster;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import io.grpc.ManagedChannelBuilder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.yein.tethys.cluster.ClusterNode;
import top.yein.tethys.cluster.ClusterNodesQuery;
import top.yein.tethys.core.util.MonoSinkStreamObserver;
import top.yein.tethys.grpc.HealthCheckRequest;
import top.yein.tethys.grpc.HealthCheckResponse;
import top.yein.tethys.grpc.HealthCheckResponse.ServingStatus;
import top.yein.tethys.grpc.HealthGrpc;

/**
 * 简单的集群管理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class SimpleClusterManager implements ClusterNodesQuery, AutoCloseable {

  private final Duration CHECK_HEALTH_DURATION = Duration.ofSeconds(30);
  private AtomicBoolean RUNNING = new AtomicBoolean(true);

  private List<SimpleClusterNode> clusterNodes;

  /** @param grpcTargets */
  public SimpleClusterManager(String grpcTargets) {
    if (Strings.isNullOrEmpty(grpcTargets)) {
      throw new IllegalArgumentException("集群[grpc.targets]配置不能为空");
    }

    this.clusterNodes = this.initClusterNodes(grpcTargets);
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
  public void close() {
    if (!RUNNING.compareAndSet(true, false)) {
      return;
    }
    if (clusterNodes == null && clusterNodes.isEmpty()) {
      return;
    }
    for (SimpleClusterNode node : clusterNodes) {
      if (node.channel().isShutdown()) {
        log.warn("集群连接通道已经断开 target={}", node.target());
        return;
      }
      try {
        log.info("正在断开集群连接通道 target={}", node.target());
        node.channel().shutdownNow();
      } catch (Exception e) {
        log.error("断开集群连接通道错误 target={}", node.target(), e);
      }
    }
    log.info("集群管理已经完成清理");
  }

  @VisibleForTesting
  List<SimpleClusterNode> initClusterNodes(String grpcTargets) {
    var targets = grpcTargets.split(",");
    var nodes = new ArrayList<SimpleClusterNode>();
    for (String target : targets) {
      var channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

      var node = new SimpleClusterNode(target, channel);
      // 初始化 gRPC 存根
      node.setHealthStub(HealthGrpc.newStub(channel));
      nodes.add(node);
    }
    return ImmutableList.copyOf(nodes);
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
                log.error("集群节点健康检查状态异常[status={}] target={}", response.getStatus(), node.target());
              }
            },
            ex -> {
              node.incrementCheckFailedCount();
              log.error("集群节点健康检查异常 target={}", node.target(), ex);
            });
  }
}
