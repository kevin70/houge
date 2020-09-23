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
package io.zhudy.xim.cluster;

/**
 * 集群消息通道类型.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public enum ChannelType {
  /** 私聊消息. */
  PRIVATE("p:"),
  /** 群组消息. */
  GROUP("g:"),
  /** 系统消息. */
  SYSTEM("s:");

  private String prefix;

  ChannelType(String prefix) {
    this.prefix = prefix;
  }

  /**
   * 返回只读通道类型的前缀.
   *
   * @return 前缀
   */
  public String getPrefix() {
    return prefix;
  }
}
