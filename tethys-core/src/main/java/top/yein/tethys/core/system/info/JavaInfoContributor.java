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
package top.yein.tethys.core.system.info;

import java.util.Map;
import reactor.core.publisher.Mono;
import top.yein.tethys.system.info.Info.Builder;
import top.yein.tethys.system.info.InfoContributor;

/** @author KK (kzou227@qq.com) */
public class JavaInfoContributor implements InfoContributor {

  @Override
  public Mono<Void> contribute(Builder builder) {
    return Mono.defer(
        () -> {
          builder.withDetail(
              "java",
              Map.of(
                  "version",
                  System.getProperty("java.version"),
                  "vendor",
                  System.getProperty("java.specification.vendor")));
          return Mono.empty();
        });
  }
}
