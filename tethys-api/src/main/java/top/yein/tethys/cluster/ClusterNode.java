package top.yein.tethys.cluster;

/** @author KK (kzou227@qq.com) */
public interface ClusterNode {

  /** @return */
  int fid();

  /** @return */
  String target();

  /** @return */
  boolean isAvailable();
}
