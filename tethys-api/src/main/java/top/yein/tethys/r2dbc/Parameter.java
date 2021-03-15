package top.yein.tethys.r2dbc;

import java.util.Objects;

/** @author KK (kzou227@qq.com) */
public interface Parameter {

  /** @return */
  Object value();

  /**
   * @param <T>
   * @return
   */
  <T> Class<T> type();

  /**
   * @param value
   * @return
   */
  default Parameter from(Object value) {
    Objects.requireNonNull(value, "[value]不能为null");
    return new SampleParameter(value, value.getClass());
  }

  /**
   * @param value
   * @param type
   * @param <T>
   * @return
   */
  default Parameter from(Object value, Class<?> type) {
    Objects.requireNonNull(type, "[type]不能为null");
    return new SampleParameter(value, type);
  }
}
