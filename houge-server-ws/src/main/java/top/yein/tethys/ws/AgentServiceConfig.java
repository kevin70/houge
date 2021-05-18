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
package top.yein.tethys.ws;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 监视配置.
 *
 * @author KK (kzou227@qq.com)
 */
@Getter
@Setter
@ToString
public class AgentServiceConfig {

  /**
   * 监视消息响应 gRPC 服务的目标地址, 多个采用英文逗号分隔，WS服务会与每个 agent 单独保持链接.
   *
   * <p>默认引用环境变量 {@code TETHYS_AGENT_SERVICE_MULTI_GRPC_TARGET} 的值.
   *
   * <p>配置示例：
   *
   * <ul>
   *   <li>127.0.0.1:11012
   *   <li>dns:///foo.googleapis.com
   * </ul>
   */
  private String multiGrpcTarget;
}
