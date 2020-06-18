package io.zhudy.xim;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * {@link XimVersion}.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
class XimVersionTests {

  @Test
  void testVersion() {
    var version = XimVersion.version();
    assertThat(version).isNotEmpty();
  }
}
