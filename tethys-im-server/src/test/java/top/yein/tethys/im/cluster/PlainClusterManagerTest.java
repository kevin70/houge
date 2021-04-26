package top.yein.tethys.im.cluster;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

/**
 * {@link PlainClusterManager}单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class PlainClusterManagerTest {

  @Test
  void queryNodes() {
    var grpcTargets = "127.0.0.1:11012";
    var clusterManager = new PlainClusterManager(grpcTargets);
    var p = clusterManager.queryNodes();
    StepVerifier.create(p)
        .consumeNextWith(clusterNode -> assertThat(clusterNode.target()).isEqualTo(grpcTargets))
        .expectComplete()
        .verify();
  }

  @Test
  void queryAvailableNodes() {
    var grpcTargets = "127.0.0.1:11012";
    var clusterManager = new PlainClusterManager(grpcTargets);
    var p = clusterManager.queryAvailableNodes();
    StepVerifier.create(p).expectComplete().verify();
  }

  @Test
  void close() {
    var grpcTargets = "127.0.0.1:11012";
    var clusterManager = new PlainClusterManager(grpcTargets);
    clusterManager.close();
  }
}
