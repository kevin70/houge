package top.yein.tethys.im.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.im.cluster.PlainClusterManager;

/**
 * 集群模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class ClusterModule extends AbstractModule {

  private final Config config;

  public ClusterModule(Config config) {
    this.config = config;
  }

  @Singleton
  @Provides
  public PlainClusterManager clusterManager() {
    var grpcTargets = config.getString(ConfigKeys.CLUSTER_GRPC_TARGETS);
    return new PlainClusterManager(grpcTargets);
  }
}
