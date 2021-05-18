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
package cool.houge.system.info;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 携带应用程序的信息.
 *
 * <p>每个细节元素可以是单一的，也可以是层次对象，例如 POJO 或嵌套的 Map.
 *
 * @author KK (kzou227@qq.com)
 */
public final class Info {

  private final Map<String, Object> details;

  private Info(Map<String, Object> details) {
    this.details = Collections.unmodifiableMap(details);
  }

  /**
   * 返回一个不可变的详细信息的 Map.
   *
   * @return 详细信息
   */
  public Map<String, Object> getDetails() {
    return this.details;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Info) {
      Info other = (Info) obj;
      return this.details.equals(other.details);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.details.hashCode();
  }

  @Override
  public String toString() {
    return getDetails().toString();
  }

  /** 用于创建一个不可变的 {@link Info} 实例的生成器. */
  public static class Builder {

    private final Map<String, Object> content;

    /** 默认构造函数. */
    public Builder() {
      this.content = new ConcurrentHashMap<>();
    }

    /**
     * 使用给定的 {@code key} 和 {@code value} 记录详细信息.
     *
     * @param key 详细信息的 {@code key}
     * @param value 详细信息的 {@code value}
     * @return {@link Builder} 实例
     */
    public Builder withDetail(String key, Object value) {
      this.content.put(key, value);
      return this;
    }

    /**
     * 记录几个细节.
     *
     * @param details 详细信息
     * @return {@link Builder} 实例
     */
    public Builder withDetails(Map<String, Object> details) {
      this.content.putAll(details);
      return this;
    }

    /**
     * 返回一个不可变的 {@link Info} 实例.
     *
     * @return {@link Info} 实例
     */
    public Info build() {
      return new Info(this.content);
    }
  }
}
