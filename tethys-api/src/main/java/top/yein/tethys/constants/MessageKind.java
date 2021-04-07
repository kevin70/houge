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
package top.yein.tethys.constants;

import top.yein.tethys.EnumLite;

/**
 * 消息类型枚举.
 *
 * <p>{@link #UNRECOGNIZED} 是枚举的空值.
 *
 * @author KK (kzou227@qq.com)
 */
public enum MessageKind implements EnumLite {

  /** 不认识未被承认的枚举. */
  UNRECOGNIZED(-1, false, false),
  /** 私聊消息. */
  P_MESSAGE(0, false, false),
  /** 群组消息. */
  G_MESSAGE(1, true, false),
  /** 系统消息<b>单人</b>. */
  SP_MESSAGE(8, false, true),
  /** 系统消息<b>群组</b>. */
  SG_MESSAGE(9, true, true),
  ;

  private final int code;
  private final boolean group;
  private final boolean system;

  MessageKind(int code, boolean group, boolean system) {
    this.code = code;
    this.group = group;
    this.system = system;
  }

  @Override
  public int getCode() {
    return this.code;
  }

  /**
   * 返回枚举类型是否为群组消息.
   *
   * @return true/false
   */
  public boolean isGroup() {
    return group;
  }

  /**
   * 返回枚举类型是否为系统消息.
   *
   * @return true/false
   */
  public boolean isSystem() {
    return system;
  }

  /**
   * 将给定数值转换为枚举.
   *
   * <p>如果给定的数值未查找到对应的枚举则会返回 {@link #UNRECOGNIZED}.
   *
   * @param code 对应枚举项的数值
   * @return 与给定数值关联的枚举
   */
  public static MessageKind forCode(Integer code) {
    if (code == null) {
      return UNRECOGNIZED;
    }
    if (code == P_MESSAGE.code) {
      return P_MESSAGE;
    }
    if (code == G_MESSAGE.code) {
      return G_MESSAGE;
    }
    if (code == SP_MESSAGE.code) {
      return SP_MESSAGE;
    }
    if (code == SG_MESSAGE.code) {
      return SG_MESSAGE;
    }
    return UNRECOGNIZED;
  }
}
