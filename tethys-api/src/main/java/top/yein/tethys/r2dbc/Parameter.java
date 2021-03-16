package top.yein.tethys.r2dbc;

import java.util.Objects;

/**
 * {@link R2dbcClient.ExecuteSpec} 执行规范中 SQL 参数类型.
 *
 * @author KK (kzou227@qq.com)
 */
public class Parameter {

  private final Object value;
  private final Class<?> type;

  Parameter(Object value, Class<?> type) {
    this.value = value;
    this.type = type;
  }

  /**
   * 返回 SQL 参数值.
   *
   * @return 参数值
   */
  public Object value() {
    return this.value;
  }

  /**
   * 返回 SQL 参数映射的 Java 类型.
   *
   * @return 类型
   */
  public Class<?> type() {
    return this.type;
  }

  /**
   * 返回 {@code value } 是否为 NULL.
   *
   * @return true/false
   */
  public boolean isNull() {
    return this.value == null;
  }

  /**
   * 使用参数值构建 {@link Parameter} 参数类型默认设置为 {@code value.getClass()}.
   *
   * @param value 参数值且不能为 NULL
   * @return SQL 参数映射
   */
  public static Parameter from(Object value) {
    Objects.requireNonNull(value, "[value]不能为null");
    return new Parameter(value, value.getClass());
  }

  /**
   * 使用参数值与数据类型构建 {@link Parameter}.
   *
   * @param value 参数值
   * @param type 数据类型
   * @return SQL 参数映射
   */
  public static Parameter fromOrNull(Object value, Class<?> type) {
    Objects.requireNonNull(type, "[type]不能为null");
    return new Parameter(value, type);
  }
}
