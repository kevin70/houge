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

/**
 * 应用配置键名称定义.
 *
 * @author KK (kzou227@qq.com)
 */
public final class ConfigKeys {

  private ConfigKeys() {}

  /**
   * IM 服务开放访问的地址.
   *
   * <p>地址中可包含 `IP` 及 `PORT`.
   *
   * <p>配置示例:
   *
   * <ul>
   *   <li>:8888
   *   <li>192.168.1.5:8888
   * </ul>
   */
  public static final String IM_SERVER_ADDR = "im-server.addr";

  /**
   * REST 服务开放访问的地址.
   *
   * <p>地址中可包含 `IP` 及 `PORT`.
   *
   * <p>配置示例:
   *
   * <ul>
   *   <li>:8888
   *   <li>192.168.1.5:8888
   * </ul>
   */
  public static final String REST_SERVER_ADDR = "rest-server.addr";

  /** 内部服务交互 BASIC 认证配置. */
  public static final String SERVICE_AUTH_BASIC = "service-auth.basic";

  /**
   * 消息可拉取的最早时间限制.
   *
   * <p>最早时间 = 当前时间 - ${pull-begin-time-limit}.
   *
   * <p>默认仅可查询与当前时间相差 72 小时内的消息.
   */
  public static final String MESSAGE_PULL_BEGIN_TIME_LIMIT = "message.pull-begin-time-limit";

  /**
   * 消息存储的数据库链接.
   *
   * <p>示例：
   *
   * <p>{@code r2dbc:postgresql://[<username>:<password>@]<host>:5432/<database>}
   */
  public static final String MESSAGE_STORAGE_R2DBC_URL = "message-storage.r2dbc.url";

  /** 自动填充消息 ID 的配置开关. */
  public static final String MESSAGE_AUTOFILL_ID = "message.autofill.id";
}
