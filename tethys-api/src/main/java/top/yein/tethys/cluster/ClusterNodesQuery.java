package top.yein.tethys.cluster;

import java.util.List;

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
  List<ClusterNode> queryNodes();
}
