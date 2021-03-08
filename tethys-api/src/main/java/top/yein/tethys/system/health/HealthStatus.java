package top.yein.tethys.system.health;

import java.util.Objects;

/**
 * 表示组件或子系统状态的值对象.
 *
 * <p>Status为{@link #UP}等常用状态提供了方便的常量, {@link #DOWN}或{@link #OUT_OF_SERVICE}.
 *
 * <p>还可以在整个Tethys健康子系统中创建和使用自定义状态.
 *
 * @author KK (kzou227@qq.com)
 */
public final class HealthStatus implements Comparable<HealthStatus> {

  /** {@link HealthStatus} 表示组件或子系统处于未知状态. */
  public static final HealthStatus UNKNOWN = new HealthStatus("UNKNOWN");

  /** {@link HealthStatus} 表示组件或子系统按预期运行. */
  public static final HealthStatus UP = new HealthStatus("UP");

  /** {@link HealthStatus} 表示组件或子系统发生意外故障. */
  public static final HealthStatus DOWN = new HealthStatus("DOWN");

  /** {@link HealthStatus} 表示组件或子系统已停止使用，不应使用. */
  public static final HealthStatus OUT_OF_SERVICE = new HealthStatus("OUT_OF_SERVICE");

  private final String status;
  private final String description;

  /**
   * 使用给定的状态码和空描述创建一个新的{@link HealthStatus}实例。
   *
   * @param status 状态码
   */
  public HealthStatus(String status) {
    this(status, "");
  }

  /**
   * 使用给定的状态码和描述创建一个新的{@link HealthStatus}实例。
   *
   * @param status 状态码
   * @param description 描述
   */
  public HealthStatus(String status, String description) {
    Objects.requireNonNull(status, "code 不能为空");
    Objects.requireNonNull(description, "description 不能为空");
    this.status = status;
    this.description = description;
  }

  /**
   * 返回状态码.
   *
   * @return 状态码
   */
  public String getStatus() {
    return this.status;
  }

  /**
   * 返回描述.
   *
   * @return 描述
   */
  public String getDescription() {
    return this.description;
  }

  @Override
  public int compareTo(HealthStatus o) {
    return this.status.compareTo(o.status);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof HealthStatus) {
      return Objects.equals(this.status, ((HealthStatus) obj).status);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.status.hashCode();
  }

  @Override
  public String toString() {
    return this.status;
  }
}
