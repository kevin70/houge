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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * 业务增强异常.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public final class BizCodeException extends RuntimeException {

  private final transient BizCode bizCode;
  private final transient List<ContextValue> contextEntries = new ArrayList<>();

  /**
   * 使用业务错误码构建异常.
   *
   * @param bizCode 业务错误码
   */
  public BizCodeException(@Nonnull final BizCode bizCode) {
    this.bizCode = bizCode;
  }

  /**
   * 使用业务错误码与原因构建异常.
   *
   * @param bizCode 业务错误码
   * @param cause 原因
   */
  public BizCodeException(@Nonnull final BizCode bizCode, @Nonnull final Throwable cause) {
    super(cause);
    this.bizCode = bizCode;
  }

  /**
   * 使用业务错误码与描述构建异常.
   *
   * @param bizCode 业务错误码
   * @param message 描述
   */
  public BizCodeException(@Nonnull final BizCode bizCode, @Nonnull final String message) {
    super(message);
    this.bizCode = bizCode;
  }

  /**
   * 使用业务错误码, 描述与原因构建异常.
   *
   * @param bizCode 业务错误码
   * @param message 描述
   * @param cause 原因
   */
  public BizCodeException(
      @Nonnull final BizCode bizCode,
      @Nonnull final String message,
      @Nonnull final Throwable cause) {
    super(message, cause);
    this.bizCode = bizCode;
  }

  /**
   * 返回业务错误码.
   *
   * @return 业务错误码
   */
  public BizCode getBizCode() {
    return bizCode;
  }

  /**
   * 添加错误上下文属性.
   *
   * @param label 上下文属性标签
   * @param value 上下文属性值
   * @return 当前实例
   */
  public BizCodeException addContextValue(@Nonnull final String label, Object value) {
    contextEntries.add(new ContextValue(label, value));
    return this;
  }

  /**
   * 返回错误上下文属性.
   *
   * @return 错误上下文属性
   */
  public List<ContextValue> getContextEntries() {
    return Collections.unmodifiableList(contextEntries);
  }

  /**
   * 返回原始的错误描述.
   *
   * @return 错误描述
   */
  public String getRawMessage() {
    return super.getMessage();
  }

  /**
   * 返回格式化的错误描述.
   *
   * @return 错误描述
   */
  @Override
  public String getMessage() {
    return getFormattedMessage(super.getMessage());
  }

  /**
   * 返回格式化的错误描述.
   *
   * @return 错误描述
   */
  @Override
  public String toString() {
    return getFormattedMessage(super.getMessage());
  }

  private String getFormattedMessage(final String baseMessage) {
    final StringBuilder builder = new StringBuilder(64);
    builder.append("BizCodeException:");
    if (baseMessage != null) {
      builder.append(' ').append(baseMessage);
    }
    builder
        .append("\n\t[code=")
        .append(this.bizCode.getCode())
        .append(", message=")
        .append(this.bizCode.getMessage())
        .append(']');

    if (!contextEntries.isEmpty()) {
      builder.append("\nContext:\n");

      int i = 0;
      for (final ContextValue cv : contextEntries) {
        builder.append("\t[");
        builder.append(++i);
        builder.append(':');
        builder.append(cv.getLabel());
        builder.append("=");
        final Object value = cv.getValue();
        if (value == null) {
          builder.append("null");
        } else {
          try {
            builder.append(value.toString());
          } catch (final Exception e) {
            builder.append("Exception thrown on toString(): ").append(e.getMessage());
          }
        }
        builder.append("]\n");
      }
      builder.append("---------------------------------");
    }
    return builder.toString();
  }

  /** 错误上下文属性. */
  public static class ContextValue {

    private final String label;
    private final Object value;

    private ContextValue(final String label, final Object value) {
      this.label = label;
      this.value = value;
    }

    /**
     * 返回上下文属性标签.
     *
     * @return 上下文属性标签
     */
    public String getLabel() {
      return label;
    }

    /**
     * 返回上下文属性值.
     *
     * @return 上下文属性值
     */
    public Object getValue() {
      return value;
    }
  }
}
