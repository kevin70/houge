package io.zhudy.xim.session.impl;

import static org.assertj.core.api.Assertions.assertThat;

import io.zhudy.xim.session.impl.DefaultSessionIdGenerator;
import org.junit.jupiter.api.Test;

/** @author Kevin Zou (kevinz@weghst.com) */
class DefaultSessionIdGeneratorTests {

  @Test
  void nextId() {
    var dsig = new DefaultSessionIdGenerator();
    var sessionId = dsig.nextId();
    assertThat((int) sessionId).isEqualTo(1);
  }
}
