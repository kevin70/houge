package top.yein.tethys.cluster;

import lombok.Data;

/**
 * 集群成员信息.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class ClusterNode {

  /** 集群服务名称. */
  private String name;
  /** 集群服务主机. */
  private String host;
  /** 集群服务端口. */
  private int port;
  /** 与当前服务实例相同. */
  private boolean self;
}
