package top.yein.tethys.cluster;

/**
 * 集群节点接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface ClusterNode {

  /**
   * 节点目标地址.
   *
   * @return
   */
  String target();

  /**
   * 节点是否可用.
   *
   * @return 节点是否可用
   */
  boolean isAvailable();
}
