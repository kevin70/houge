package top.yein.tethys.im.configuration;

import static top.yein.tethys.packet.Namespaces.NS_GROUP_MESSAGE;
import static top.yein.tethys.packet.Namespaces.NS_GROUP_SUBSCRIBE;
import static top.yein.tethys.packet.Namespaces.NS_GROUP_UNSUBSCRIBE;
import static top.yein.tethys.packet.Namespaces.NS_PING;
import static top.yein.tethys.packet.Namespaces.NS_PRIVATE_MESSAGE;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.resource.AuthInterceptor;
import top.yein.tethys.im.handler.GroupMessageHandler;
import top.yein.tethys.im.handler.GroupSubscribeHandler;
import top.yein.tethys.im.handler.GroupUnsubscribeHandler;
import top.yein.tethys.im.handler.PingHandler;
import top.yein.tethys.im.handler.PrivateMessageHandler;
import top.yein.tethys.im.server.ImServer;
import top.yein.tethys.im.server.PacketDispatcher;
import top.yein.tethys.im.server.WebsocketHandler;

/** @author KK (kzou227@qq.com) */
@Configuration(proxyBeanMethods = false)
public class ImConfiguration {

  /**
   * @param pingHandler
   * @param privateMessageHandler
   * @param groupMessageHandler
   * @param groupSubscribeHandler
   * @param groupUnsubscribeHandler
   * @return
   */
  @Bean
  public PacketDispatcher packetDispatcher(
      PingHandler pingHandler,
      PrivateMessageHandler privateMessageHandler,
      GroupMessageHandler groupMessageHandler,
      GroupSubscribeHandler groupSubscribeHandler,
      GroupUnsubscribeHandler groupUnsubscribeHandler) {
    return new PacketDispatcher(
        Map.of(
            NS_PING,
            pingHandler,
            NS_PRIVATE_MESSAGE,
            privateMessageHandler,
            NS_GROUP_MESSAGE,
            groupMessageHandler,
            NS_GROUP_SUBSCRIBE,
            groupSubscribeHandler,
            NS_GROUP_UNSUBSCRIBE,
            groupUnsubscribeHandler));
  }

  /**
   * @param authService
   * @return
   */
  @Bean
  public Interceptors interceptors(AuthService authService) {
    var authInterceptor = new AuthInterceptor(authService);
    return new Interceptors(authInterceptor::handle);
  }

  /**
   * @param addr
   * @param websocketHandler
   * @param interceptors
   * @return
   */
  @Bean
  public ImServer imServer(
      @Value("${im-server.addr}") String addr,
      WebsocketHandler websocketHandler,
      Interceptors interceptors) {
    return new ImServer(addr, websocketHandler, interceptors);
  }
}
