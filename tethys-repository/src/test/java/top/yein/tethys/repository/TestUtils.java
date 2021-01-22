package top.yein.tethys.repository;

import top.yein.tethys.util.YeinGid;

/**
 * 测试工具包.
 *
 * @author KK (kzou227@qq.com)
 */
final class TestUtils {

  /**
   * 返回消息 ID.
   *
   * @return 消息 ID
   */
  static String newMessageId() {
    return new YeinGid(0).toHexString();
  }
}
