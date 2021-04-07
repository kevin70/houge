package top.yein.tethys.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * {@link MessageKind} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class MessageKindTest {

  @Test
  void forCode() {
    for (MessageKind value : MessageKind.values()) {
      assertThat(MessageKind.forCode(value.getCode())).isEqualTo(value);
    }
    assertThat(MessageKind.forCode(null)).isEqualTo(MessageKind.UNRECOGNIZED);
    assertThat(MessageKind.forCode(9999)).isEqualTo(MessageKind.UNRECOGNIZED);
  }
}
