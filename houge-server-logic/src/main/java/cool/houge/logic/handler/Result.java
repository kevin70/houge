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
package cool.houge.logic.handler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import javax.annotation.Nullable;

/**
 * 响应结果.
 *
 * @author KK (kzou227@qq.com)
 */
public class Result {

  @JsonIgnore private boolean failed;
  @JsonUnwrapped private Object ok;
  private Error error;

  /**
   * 返回是否为错误的结果返回.
   *
   * @return true/false
   */
  public boolean isError() {
    return failed;
  }

  /**
   * 返回成功的响应结果.
   *
   * @return 响应结果
   */
  @Nullable
  public Object getOk() {
    return ok;
  }

  /**
   * 返回错误的响应结果.
   *
   * @return 响应结果
   */
  @Nullable
  public Error getError() {
    return error;
  }

  /**
   * 创建一个成功的结果返回.
   *
   * <p>该接口无法设置响应结果.
   *
   * @return 响应结果
   */
  public static Result ok() {
    var result = new Result();
    return result;
  }

  /**
   * 创建一个成功的带数据的结果返回.
   *
   * @param data 响应数据
   * @return 响应结果
   */
  public static Result ok(Object data) {
    var result = new Result();
    result.ok = data;
    return result;
  }

  /**
   * 创建一个错误的结果.
   *
   * @param code 错误码
   * @param reason 错误原因
   * @return 响应结果
   */
  public static Result error(int code, String reason) {
    return error(code, reason, null);
  }

  /**
   * 创建一个错误的结果.
   *
   * @param code 错误码
   * @param reason 错误原因
   * @param details 错误详细
   * @return 响应结果
   */
  public static Result error(int code, String reason, Object details) {
    var result = new Result();
    result.failed = true;
    result.error = new Error(code, reason, details);
    return result;
  }

  /** 错误响应. */
  public static class Error {

    private int code;
    private String reason;
    private Object details;

    private Error(int code, String reason, Object details) {
      this.code = code;
      this.reason = reason;
    }

    /**
     * 返回错误码.
     *
     * @return 错误码.
     */
    public int getCode() {
      return code;
    }

    /**
     * 返回错误原因.
     *
     * @return 错误原因
     */
    public String getReason() {
      return reason;
    }

    /**
     * 返回错误详细.
     *
     * @return 错误详细
     */
    public Object getDetails() {
      return details;
    }
  }
}
