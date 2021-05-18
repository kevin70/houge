/*
 * Copyright 2019-2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.yein.tethys;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import top.yein.tethys.Env;

/**
 * {@link Env} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class EnvTest {

  @Test
  void defaultEnv() {
    assertThat(Env.current()).isEqualTo(Env.PROD);
    assertThat(Env.current()).isEqualTo(Env.PROD);
  }

  @Test
  void defaultEnvProd() throws Exception {
    restoreSystemProperties(
        () -> {
          System.clearProperty(Env.SYSTEM_PROP_VAR_NAME);
          assertThat(Env.getEnv()).isEqualTo(Env.PROD);
        });
  }

  @Test
  void illegalEnv() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty(Env.SYSTEM_PROP_VAR_NAME, "illegal");
          assertThatIllegalArgumentException().isThrownBy(() -> Env.getEnv());
        });
  }

  @Test
  void fromSystemEnv() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty(Env.SYSTEM_PROP_VAR_NAME, Env.TEST.name());
          assertThat(Env.getEnv()).isEqualTo(Env.TEST);
        });
  }

  @Test
  void ignoreCase() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty(Env.SYSTEM_PROP_VAR_NAME, "pRod");
          assertThat(Env.getEnv()).isEqualTo(Env.PROD);
        });
  }

  @Test
  void fromEnvironmentVariable() throws Exception {
    withEnvironmentVariable(Env.SYSTEM_ENV_VAR_NAME, "test")
        .execute(() -> assertThat(Env.getEnv()).isEqualTo(Env.TEST));
  }
}
