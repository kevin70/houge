/**
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
package io.zhudy.xim.packet;

import lombok.Value;

/**
 * 错误消息.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Value
public class ErrorPacket implements OpsPacket {

  /** 错误信息描述. */
  String message;
  /** 错误信息详细信息. */
  String details;

  @Override
  public String getNs() {
    return Namespaces.ERROR;
  }
}
