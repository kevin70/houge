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
package top.yein.tethys.constants;

/**
 * 消息读取状态.
 *
 * @author KK (kzou227@qq.com)
 */
public enum MessageReadStatus {

  /** 没有状态. */
  NONE(-1),
  /** 消息已读状态. */
  READ(0),
  /** 消息未读状态. */
  UNREAD(1),
  ;
  private final int code;

  MessageReadStatus(int code) {
    this.code = code;
  }

  /**
   * 返回消息读取状态码.
   *
   * @return 状态码
   */
  public int getCode() {
    return code;
  }
}
