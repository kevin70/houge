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
package io.zhudy.xim;

/**
 * 业务错误码定义.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public enum BizCodes implements BizCode {
  C0("未知错误"),
  C401("认证失败"),
  C404("未找到"),

  // ---------------------------------------------------------------------//
  // 3300 - 3350 >> 会话 TOKEN 相关的错误码
  // ---------------------------------------------------------------------//
  C3300("非法的令牌"),
  C3301("已过期的令牌"),
  C3302("未成熟的令牌"),
  C3305("JWT 参数错误"),

  // ---------------------------------------------------------------------//
  // 3500 - 3550 >> 会话 SESSION 相关的错误码
  // ---------------------------------------------------------------------//
  C3500("sessionId 冲突"),

  // ---------------------------------------------------------------------//
  // 3600 - 3700 >> Packet 相关的错误码
  // ---------------------------------------------------------------------//
  C3600("缺少必须的参数"),
  ;
  private final int code;
  private final String message;

  BizCodes(String message) {
    this.code = Integer.parseInt(this.name().substring(1));
    this.message = message;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
