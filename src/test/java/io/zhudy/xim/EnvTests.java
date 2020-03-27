package io.zhudy.xim;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

/** @author Kevin Zou (kevinz@weghst.com) */
public class EnvTests {

  @Test
  public void defaultEnv() {
    assertThat(Env.current()).isEqualTo(Env.INTEGRATION_TEST);
    assertThat(Env.current()).isEqualTo(Env.INTEGRATION_TEST);
  }

  @Test
  public void defaultEnvProd() throws Exception {
    restoreSystemProperties(
        () -> {
          System.clearProperty("xim.env");
          assertThat(Env.getEnv()).isEqualTo(Env.PROD);
        });
  }

  @Test
  public void illegalEnv() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty("xim.env", "illegal");
          assertThatIllegalArgumentException().isThrownBy(() -> Env.getEnv());
        });
  }

  @Test
  public void fromSystemEnv() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty("xim.env", Env.TEST.name());
          assertThat(Env.getEnv()).isEqualTo(Env.TEST);
        });
  }

  @Test
  public void ignoreCase() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty("xim.env", "pRod");
          assertThat(Env.getEnv()).isEqualTo(Env.PROD);
        });
  }

  @Test
  public void fromEnvironmentVariable() throws Exception {
    withEnvironmentVariable("XIM_ENV", "test")
        .execute(() -> assertThat(Env.getEnv()).isEqualTo(Env.TEST));
  }
}
