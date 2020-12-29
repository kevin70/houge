package top.yein.tethys.core.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author KK (kzou227@qq.com)
 * @date 2020-12-29 21:27
 */
public class HttpExceptionHandler extends ChannelDuplexHandler {

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    System.out.println(ctx);
    ctx.fireExceptionCaught(cause);
  }
}
