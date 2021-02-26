package top.yein.tethys.system.info;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
      this.content = new LinkedHashMap<>();
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
