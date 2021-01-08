package top.yein.tethys.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * {@link Version} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class VersionTest {

  @Test
  void version() {
    var ver = Version.version();
    assertThat(ver).isNotNull();
  }
}
