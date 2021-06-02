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
package cool.houge.system.health;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 携带有关组件或子系统运行状况的信息.
 *
 * <p>使用{@link Health.Builder}流API生成{@link Health}实例.
 *
 * <p>{@link HealthIndicator}的典型用法是:
 *
 * <pre class="code">
 * try {
 * 	// do some test to determine state of component
 * 	return Health.up().withDetail("version", "1.1.2").build();
 * }
 * catch (Exception ex) {
 * 	return Health.down(ex).build();
 * }
 * </pre>
 *
 * @author KK (kzou227@qq.com)
 */
public final class Health {

  private final String componentName;
  private final @JsonUnwrapped HealthStatus status;
  private final ImmutableMap<String, Object> details;

  private Health(String componentName, HealthStatus status, Map<String, Object> details) {
    this.componentName = componentName;
    this.status = status;
    this.details = ImmutableMap.copyOf(details);
  }

  /**
   * 返回组件名称.
   *
   * @return 组件名称
   */
  public String getComponentName() {
    return componentName;
  }

  /**
   * 返回健康状况.
   *
   * @return 永远不为空的健康状态
   */
  public HealthStatus getStatus() {
    return this.status;
  }

  /**
   * 返回健康详细信息.
   *
   * @return 详细信息或者空Map
   */
  public ImmutableMap<String, Object> getDetails() {
    return this.details;
  }

  /**
   * 返回一个新的{@link Health}并删除全部的详细信息.
   *
   * @return 没有详细信息的 {@code Health}
   */
  Health withoutDetails() {
    if (this.details.isEmpty()) {
      return this;
    }
    return new Health(componentName, status, ImmutableMap.of());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Health) {
      Health other = (Health) obj;
      return this.componentName.equals(other.componentName)
          && this.status.equals(other.status)
          && this.details.equals(other.details);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hashCode = this.status.hashCode();
    return 13 * hashCode + this.details.hashCode();
  }

  @Override
  public String toString() {
    return getStatus() + " " + getDetails();
  }

  /** 用于创建不可变的{@link Health}实例的生成器. */
  public static class Builder {

    private String componentName;
    private HealthStatus status;
    private Map<String, Object> details;

    /**
     * 创建{@link Builder}实例.
     *
     * @param componentName 组件名称
     */
    public Builder(String componentName) {
      this(componentName, HealthStatus.UNKNOWN, Map.of());
    }

    /**
     * 创建{@link Builder}实例, 将健康状态设置为给定的{@code status}.
     *
     * @param componentName 组件名称
     * @param status 健康状态
     */
    public Builder(String componentName, HealthStatus status) {
      this(componentName, status, Map.of());
    }

    /**
     * 创建新的生成器实例, 将 status 设置为给定的 {@code status}, 将 details 设置为给定的 {@code details}.
     *
     * @param componentName 组件名称
     * @param status 健康状态
     * @param details 详细信息
     */
    public Builder(String componentName, HealthStatus status, Map<String, ?> details) {
      Objects.requireNonNull(componentName, "[componentName]不能为null");
      Objects.requireNonNull(status, "[status]不能为null");
      Objects.requireNonNull(details, "[details]不能为null");
      this.componentName = componentName;
      this.status = status;
      this.details = new LinkedHashMap<>(details);
    }

    /**
     * 给定的 {@link Throwable} 记录详细信息.
     *
     * @param ex 异常
     * @return {@link Builder} 实例
     */
    public Builder withException(Throwable ex) {
      Objects.requireNonNull(ex, "[ex]不能为null");
      return withDetail("error", ex.getClass().getName() + ": " + ex.getMessage());
    }

    /**
     * Record detail using given {@code key} and {@code value}.
     *
     * <p>使用给定的{@code key}和{@code value}记录详细信息.
     *
     * @param key 详细信息的key
     * @param value 详细信息的value
     * @return {@link Builder} 实例
     */
    public Builder withDetail(String key, Object value) {
      Objects.requireNonNull(key, "[key]不能为null");
      Objects.requireNonNull(value, "[value]不能为null");
      this.details.put(key, value);
      return this;
    }

    /**
     * 从给定的{@code details}映射记录详细信息.
     *
     * <p>如果存在重复项, 则给定{@code Map}中的键将替换任何现有键.
     *
     * @param details 详细信息的Map
     * @return this {@link Builder} instance
     * @return {@link Builder} 实例
     */
    public Builder withDetails(Map<String, ?> details) {
      Objects.requireNonNull(details, "[details]不能为null");
      this.details.putAll(details);
      return this;
    }

    /**
     * 将健康状态设置为{@link HealthStatus#UNKNOWN}.
     *
     * @return {@link Builder} 实例
     */
    public Builder unknown() {
      return status(HealthStatus.UNKNOWN);
    }

    /**
     * 将健康状态设置为{@link HealthStatus#UP}.
     *
     * @return {@link Builder} 实例
     */
    public Builder up() {
      return status(HealthStatus.UP);
    }

    /**
     * 将健康状态设置为{@link HealthStatus#UP}同时将详细信息设置为给定的{@link Throwable}.
     *
     * @param ex 异常
     * @return {@link Builder} 实例
     */
    public Builder down(Throwable ex) {
      return down().withException(ex);
    }

    /**
     * 将健康状态设置为{@link HealthStatus#DOWN}.
     *
     * @return {@link Builder} 实例
     */
    public Builder down() {
      return status(HealthStatus.DOWN);
    }

    /**
     * 将健康状态设置为{@link HealthStatus#OUT_OF_SERVICE}.
     *
     * @return {@link Builder} 实例
     */
    public Builder outOfService() {
      return status(HealthStatus.OUT_OF_SERVICE);
    }

    /**
     * 将健康状态设置为给定的 {@code statusCode}.
     *
     * @param statusCode 健康状态码
     * @return {@link Builder} 实例
     */
    public Builder status(String statusCode) {
      return status(new HealthStatus(statusCode));
    }

    /**
     * 将健康状态设置为给定的 {@code status}.
     *
     * @param status 健康状态
     * @return {@link Builder} 实例
     */
    public Builder status(HealthStatus status) {
      this.status = status;
      return this;
    }

    /**
     * 使用先前指定的健康状态和详细信息创建一个新的{@link Health}实例.
     *
     * @return {@link Health}实例
     */
    public Health build() {
      return new Health(this.componentName, this.status, this.details);
    }
  }
}
