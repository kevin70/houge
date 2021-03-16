package top.yein.tethys.r2dbc;

import io.r2dbc.spi.R2dbcException;

/**
 * R2DBC 错误的结果数量异常.
 *
 * @author KK (kzou227@qq.com)
 */
public class IncorrectResultSizeException extends R2dbcException {

  private final int expectedSize;

  /**
   * 使用预期结果数量构建异常.
   *
   * @param expectedSize 预期结果数量
   */
  public IncorrectResultSizeException(int expectedSize) {
    super("Incorrect result size: expected " + expectedSize);
    this.expectedSize = expectedSize;
  }

  /**
   * 使用描述和预期结果数据构建异常.
   *
   * @param message 描述
   * @param expectedSize 预期结果数量
   */
  public IncorrectResultSizeException(String message, int expectedSize) {
    super(message);
    this.expectedSize = expectedSize;
  }

  /**
   * 返回预期结果数量.
   *
   * @return 预期结果数量
   */
  public int getExpectedSize() {
    return expectedSize;
  }
}
