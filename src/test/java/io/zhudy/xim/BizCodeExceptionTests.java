package io.zhudy.xim;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** @author Kevin Zou (kevinz@weghst.com) */
class BizCodeExceptionTests {

  @Test
  public void t1() {
    var e = new BizCodeException(BizCodes.C0);
    assertThat(e.getBizCode()).isEqualTo(BizCodes.C0);
  }

  @Test
  public void t2() {
    var message = "custom message";
    var e = new BizCodeException(BizCodes.C0, message);
    assertThat(e.getBizCode()).isEqualTo(BizCodes.C0);
    assertThat(e.getRawMessage()).isEqualTo(message);
  }

  @Test
  public void t3() {
    var cause = new IllegalArgumentException();
    var e = new BizCodeException(BizCodes.C0, cause);
    assertThat(e.getBizCode()).isEqualTo(BizCodes.C0);
    assertThat(e.getCause()).isEqualTo(cause);
  }

  @Test
  public void t4() {
    var message = "custom message";
    var cause = new IllegalArgumentException();
    var e = new BizCodeException(BizCodes.C0, message, cause);
    assertThat(e.getBizCode()).isEqualTo(BizCodes.C0);
    assertThat(e.getRawMessage()).isEqualTo(message);
    assertThat(e.getCause()).isEqualTo(cause);
  }

  @Test
  public void t5() {
    var e =
        new BizCodeException(BizCodes.C0)
            .addContextValue("label 1", "value 1")
            .addContextValue("label 2", "value 2");

    var entries = e.getContextEntries();
    assertThat(entries).hasSize(2);

    var first = entries.get(0);
    assertThat(first)
        .hasFieldOrPropertyWithValue("label", "label 1")
        .hasFieldOrPropertyWithValue("value", "value 1");

    var second = entries.get(1);
    assertThat(second)
        .hasFieldOrPropertyWithValue("label", "label 2")
        .hasFieldOrPropertyWithValue("value", "value 2");
  }

  @Test
  public void t6() {
    var e =
        new BizCodeException(BizCodes.C0)
            .addContextValue("label 1", "value 1")
            .addContextValue("label 2", "value 2");
    var formattedMessage = e.toString();
    assertThat(formattedMessage).contains("label 1", "value 1", "label 2", "value 2");
  }

  @Test
  public void t7() {
    var message = "custome message";
    var e =
        new BizCodeException(BizCodes.C0, message)
            .addContextValue("label 1", "value 1")
            .addContextValue("label 2", "value 2")
            .addContextValue("label 2", null);
    var formattedMessage = e.toString();
    assertThat(formattedMessage).contains(message, "label 1", "value 1", "label 2", "value 2");
  }

  @Test
  public void t8() {
    var message = "custome message";
    var e =
        new BizCodeException(BizCodes.C0, message)
            .addContextValue("label 1", "value 1")
            .addContextValue("label 2", "value 2")
            .addContextValue("label 2", null)
            .addContextValue(
                "label x",
                new Object() {
                  @Override
                  public String toString() {
                    throw new IllegalStateException("test exception");
                  }
                });
    var formattedMessage = e.toString();
    assertThat(formattedMessage)
        .contains(message, "label 1", "value 1", "label 2", "value 2", "test exception");
  }

  @Test
  public void t9() {
    var message = "custome message";
    var e = new BizCodeException(BizCodes.C0, message);
    var formattedMessage = e.toString();
    assertThat(formattedMessage).isNotBlank();
    assertThat(formattedMessage).isEqualTo(e.getMessage());
  }
}
