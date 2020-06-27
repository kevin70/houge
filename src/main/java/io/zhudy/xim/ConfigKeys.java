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
package io.zhudy.xim;

/**
 * 应用配置键名称定义.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public final class ConfigKeys {

  private ConfigKeys() {}

  /**
   * IM 服务开放访问的地址.地址中可包含 `IP` 及 `PORT`.
   *
   * <p>配置示例:
   *
   * <ul>
   *   <li>:8888
   *   <li>192.168.1.5:8888
   * </ul>
   */
  public static final String IM_SERVER_ADDR = "im-server.addr";

  /** 是否启用匿名连接. */
  public static final String IM_SERVER_ENABLED_ANONYMOUS = "im-server.enabled-anonymous";

  /**
   * JWT 密钥配置前缀.
   *
   * <p>e.g:
   *
   * <ul>
   *   <li>im-server.jwt-secrets.key1=This is secret of key1
   *   <li>im-server.jwt-secrets.keyX=This is secret of keyX
   * </ul>
   */
  public static final String IM_SERVER_AUTH_JWT_SECRETS = "im-server.auth-jwt-secrets";
}
