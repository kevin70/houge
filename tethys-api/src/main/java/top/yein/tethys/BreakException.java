package top.yein.tethys;

/**
 * 中断异常定义.
 *
 * @author KK (kzou227@qq.com)
 */
public class BreakException extends RuntimeException {

  /**
   * 使用中断描述构建对象.
   *
   * @param message 中断描述
   */
  public BreakException(String message) {
    super(message);
  }

  // 忽略异常堆栈
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
