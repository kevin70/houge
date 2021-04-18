package top.yein.tethys.util;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import reactor.netty.channel.AbortedException;

/**
 * Socket 异常工具类.
 *
 * @author KK (kzou227@qq.com)
 */
public final class SocketExceptionUtils {

  private SocketExceptionUtils() {}

  /**
   * Socket 网络通讯中可忽略记录日志的异常.
   *
   * @return true/false
   * @see IOException
   * @see SocketException
   * @see AbortedException
   * @see ClosedChannelException
   */
  public static boolean ignoreLogException(Throwable err) {
    if (err instanceof IOException) {
      var m = err.getMessage();
      if (m != null) {
        if ("Broken pipe".equals(m)) {
          return true;
        }
        if ("Connection reset by peer".equals(m)) {
          return true;
        }
        if (m.contains("远程主机强迫关闭了一个现有的连接")) {
          return true;
        }
        if (m.contains("你的主机中的软件中止了一个已建立的连接")) {
          return true;
        }
      }
    }
    if (err instanceof SocketException) {
      if ("Connection reset by peer".equals(err.getMessage())) {
        return true;
      }
    }
    if (err instanceof AbortedException) {
      if (err.getCause() != null
          && "io.netty.channel.StacklessClosedChannelException"
              .equals(err.getCause().getClass().getName())) {
        return true;
      }
      if ("Connection has been closed BEFORE send operation".equals(err.getMessage())) {
        return true;
      }
    }
    return false;
  }
}
