package top.yein.tethys.cluster;

import reactor.core.publisher.Flux;

/**
 * 群组节点查询接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface ClusterNodesQuery {

  /**
   * 返回集群节点列表.
   *
   * @return 集群节点列表
   */
  Flux<? extends ClusterNode> queryNodes();

  /**
   * 返回可用的集群节点列表.
   *
   * @return 可用的集群节点
   */
  Flux<? extends ClusterNode> queryAvailableNodes();
}
