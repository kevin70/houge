package top.yein.tethys.r2dbc;

/** @author KK (kzou227@qq.com) */
class SampleParameter implements Parameter {

  private final Object value;
  private final Class<?> type;

  SampleParameter(Object value, Class<?> type) {
    this.value = value;
    this.type = type;
  }

  @Override
  public Object value() {
    return value;
  }

  @Override
  public Class<?> type() {
    return type;
  }
}
