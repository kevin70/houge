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
 * 消息类型枚举定义.
 *
 * @author KK (kzou227@qq.com)
 */
public enum MessageKind {
  /** 普通文本消息. */
  TEXT(1),
  /** 图片消息. */
  IMAGE(2),
  /** 语音消息. */
  VOICE(3),
  /** 视频消息. */
  VIDEO(4),
  ;
  private final int code;

  MessageKind(int code) {
    this.code = code;
  }

  /**
   * 消息类型代码.
   *
   * @return 代码
   */
  public int getCode() {
    return code;
  }

  /**
   * 将类型代码转换为枚举.
   *
   * @param code 代码
   * @return 枚举
   * @throws IllegalArgumentException 非法的消息码
   */
  public static MessageKind forCode(int code) throws IllegalArgumentException {
    for (MessageKind v : values()) {
      if (v.code == code) {
        return v;
      }
    }
    throw new IllegalArgumentException("非法的 MessageKind 代码 \"" + code + "\"");
  }
}
