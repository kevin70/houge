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
          // NOSONAR
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
