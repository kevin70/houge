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
package top.yein.tethys.vo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

/**
 * 消息发送 VO.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class MessageSendVo {

  /**
   * 消息命名空间.
   *
   * <ul>
   *   <li>{@link top.yein.tethys.packet.Namespaces#NS_PRIVATE_MESSAGE}
   *   <li>{@link top.yein.tethys.packet.Namespaces#NS_GROUP_MESSAGE}
   * </ul>
   */
  private String ns;
  /**
   * 消息接收者.
   *
   * <p>当 {@code ns} 值为 {@link top.yein.tethys.packet.Namespaces#NS_PRIVATE_MESSAGE} 时, {@code to}
   * 代表<b>用户 ID</b>. 当 {@code ns} 值为 {@link top.yein.tethys.packet.Namespaces#NS_GROUP_MESSAGE} 时,
   * {@code to} 代表<b>群组 ID</b>.
   */
  private long to;
  /** 消息内容. */
  private String content;
  /** 消息内容类型. */
  private int contentKind = 0;
  /** 统一资源定位器. */
  private String url;
  /** 自定义参数. */
  private @JsonUnwrapped String customArgs;
}
