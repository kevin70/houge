/*
 * Copyright 2019-2021 the original author or authors
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
package cool.houge.system.info;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

/**
 * {@link JavaInfoContributor} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class JavaInfoContributorTest {

  @Test
  void contribute() {
    var contributor = new JavaInfoContributor();
    var builder = new Info.Builder();
    StepVerifier.create(contributor.contribute(builder)).expectComplete().verify();

    Map<String, Object> javaInfo = (Map<String, Object>) builder.build().getDetails().get("java");
    assertThat(javaInfo).containsKeys("vm_name", "version", "vendor");
  }
}
