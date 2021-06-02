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

import com.google.common.collect.ImmutableMap;
import cool.houge.system.info.Info.Builder;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * 启动应用的 Java 信息贡献者实现类.
 *
 * @author KK (kzou227@qq.com)
 */
public class JavaInfoContributor implements InfoContributor {

  @Override
  public Mono<Void> contribute(Builder builder) {
    return Mono.fromRunnable(() -> builder.withDetail("java", info0()));
  }

  private Map<String, Object> info0() {
    return ImmutableMap.<String, Object>builder()
        .put("vm_name", System.getProperty("java.vm.name"))
        .put("version", System.getProperty("java.version"))
        .put("vendor", System.getProperty("java.specification.vendor"))
        .build();
  }
}
