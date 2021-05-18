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
package cool.houge.rest;

import javax.inject.Inject;
import cool.houge.system.identifier.AbstractApplicationIdentifier;
import cool.houge.system.identifier.ServerInstanceRepository;

/**
 * REST 应用程序标识接口的实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class RestApplicationIdentifier extends AbstractApplicationIdentifier {

  /**
   * 使用应用实例数据访问对象构造 REST 应用标识对象.
   *
   * @param serverInstanceRepository 应用实例数据访问对象
   */
  @Inject
  public RestApplicationIdentifier(ServerInstanceRepository serverInstanceRepository) {
    super(serverInstanceRepository);
  }

  @Override
  public String applicationName() {
    return "tethys-rest";
  }
}
