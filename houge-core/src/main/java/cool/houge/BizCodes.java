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
package cool.houge;

import top.yein.chaos.biz.BizCode;

/**
 * 业务错误码定义.
 *
 * @author KK (kzou227@qq.com)
 */
public enum BizCodes implements BizCode {

  // ---------------------------------------------------------------------//
  // 3300 - 3350 >> 会话 TOKEN 相关的错误码
  // ---------------------------------------------------------------------//
  C3300(400, 2, "非法的访问令牌"),
  C3301(400, 2, "已过期的令牌"),
  C3302(400, 2, "claims 校验失败"),
  C3305(400, 2, "JWT 参数错误"),
  C3309(400, 2, "服务端未发现指定的 kid"),
  C3310(500, 2, "服务端未配置可用的 JWT 签名密钥"),
  C3311(500, 2, "服务器不支持的签名算法"),

  // ---------------------------------------------------------------------//
  // 3500 - 3550 >> 会话 SESSION 相关的错误码
  // ---------------------------------------------------------------------//
  C3500("sessionId 冲突"),
  C3501("私聊消息 from 与当前会话登录用户不匹配"),
  C3522(400, 12, "不支持的消息类型"),

  // ---------------------------------------------------------------------//
  // 3600 - 3700 >> Packet 相关的错误码
  // ---------------------------------------------------------------------//
  C3600(400, 3, "缺少必须的参数"),
  C3601(500, 13, "序列化 Packet 错误"),
  C3602(400, 3, "解析 Packet 错误"),
  C3630(400, 3, "消息接收方不存在"),
  ;
  private final int code;
  private final int httpStatus;
  private final int grpcStatus;
  private final String message;

  BizCodes(String message) {
    this(500, 2, message);
  }

  BizCodes(int httpStatus, int grpcStatus, String message) {
    this.grpcStatus = grpcStatus;
    this.code = Integer.parseInt(this.name().substring(1));
    this.httpStatus = httpStatus;
    this.message = message;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public int getHttpStatus() {
    return httpStatus;
  }

  @Override
  public int getGrpcStatus() {
    return grpcStatus;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
