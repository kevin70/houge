package top.yein.tethys.system.info;

/**
 * 提供其他信息详细信息.
 *
 * @author KK (kzou227@qq.com)
 */
@FunctionalInterface
public interface InfoContributor {

  /**
   * 使用指定的 {@link Info.Builder} 生成器生成其它详细信息.
   *
   * @param builder 生成器
   */
  void contribute(Info.Builder builder);
}
