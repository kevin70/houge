package top.yein.tethys.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;

/**
 * {@link HostNameUtils} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class HostNameUtilsTest {

  @Test
  void getLocalHostAddress() throws UnknownHostException {
    assertThat(HostNameUtils.getLocalHostAddress()).isNotNull();
  }

  @Test
  void getLocalHostLANAddress() throws UnknownHostException {
    assertThat(HostNameUtils.getLocalHostLANAddress()).isNotNull();
  }
}
