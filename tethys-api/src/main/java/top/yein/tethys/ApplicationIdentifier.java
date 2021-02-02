package top.yein.tethys;

/**
 * 应用程序标识符接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface ApplicationIdentifier {

  /**
   * 返回应用名称.
   *
   * @return 应用名称
   */
  String applicationName();

  /**
   * 返回应用标识 ID.
   *
   * <p>FID 应用实例在<b>集群</b>中的唯一标识符.
   *
   * @return 应用标识 ID
   */
  int fid();

  /**
   * 返回应用版本.
   *
   * @return 应用版本
   */
  String version();

  /** 清理应用数据. */
  void clean();
}
