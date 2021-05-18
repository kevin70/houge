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
package top.yein.tethys.system.info;

import java.util.Map;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.system.identifier.ApplicationIdentifier;
import top.yein.tethys.system.info.Info.Builder;

/** @author KK (kzou227@qq.com) */
public class AppInfoContributor implements InfoContributor {

  private final ApplicationIdentifier applicationIdentifier;

  @Inject
  public AppInfoContributor(ApplicationIdentifier applicationIdentifier) {
    this.applicationIdentifier = applicationIdentifier;
  }

  @Override
  public Mono<Void> contribute(Builder builder) {
    return Mono.defer(
        () -> {
          builder.withDetail(
              "app",
              Map.of(
                  "name",
                  applicationIdentifier.applicationName(),
                  "version",
                  applicationIdentifier.version(),
                  "fid",
                  applicationIdentifier.fid()));
          return Mono.empty();
        });
  }
}
