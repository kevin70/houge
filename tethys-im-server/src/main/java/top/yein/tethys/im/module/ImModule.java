package top.yein.tethys.im.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.ConfigKeys;
import top.yein.tethys.core.MessageProperties;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.id.YeinGidMessageIdGenerator;
import top.yein.tethys.core.resource.AuthInterceptor;
import top.yein.tethys.core.session.DefaultSessionGroupManager;
import top.yein.tethys.core.session.DefaultSessionManager;
import top.yein.tethys.core.session.LocalSessionIdGenerator;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.im.ImApplicationIdentifier;
import top.yein.tethys.im.handler.GroupMessageHandler;
import top.yein.tethys.im.handler.GroupSubscribeHandler;
import top.yein.tethys.im.handler.GroupUnsubscribeHandler;
import top.yein.tethys.im.handler.PingHandler;
import top.yein.tethys.im.handler.PrivateMessageHandler;
import top.yein.tethys.im.server.PacketDispatcher;
import top.yein.tethys.im.server.PacketHandler;
import top.yein.tethys.im.server.WebsocketHandler;
import top.yein.tethys.packet.Namespaces;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.session.SessionIdGenerator;
import top.yein.tethys.session.SessionManager;

/**
 * IM Guice 模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class ImModule extends AbstractModule {

  private final Config config;

  public ImModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(ApplicationIdentifier.class).to(ImApplicationIdentifier.class).in(Scopes.SINGLETON);
    bind(WebsocketHandler.class).in(Scopes.SINGLETON);
    bind(PacketDispatcher.class).in(Scopes.SINGLETON);

    bind(SessionIdGenerator.class).to(LocalSessionIdGenerator.class).in(Scopes.SINGLETON);
    bind(SessionManager.class).to(DefaultSessionManager.class).in(Scopes.SINGLETON);
    bind(SessionGroupManager.class).to(DefaultSessionGroupManager.class).in(Scopes.SINGLETON);

    // 消息 ID 生成器
    bind(MessageIdGenerator.class).to(YeinGidMessageIdGenerator.class).in(Scopes.SINGLETON);

    // PacketHandlers =========================================>>>
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_PING))
        .to(PingHandler.class)
        .in(Scopes.SINGLETON);
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_PRIVATE_MESSAGE))
        .to(PrivateMessageHandler.class)
        .in(Scopes.SINGLETON);
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_GROUP_MESSAGE))
        .to(GroupMessageHandler.class)
        .in(Scopes.SINGLETON);
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_GROUP_SUBSCRIBE))
        .to(GroupSubscribeHandler.class)
        .in(Scopes.SINGLETON);
    bind(PacketHandler.class)
        .annotatedWith(Names.named(Namespaces.NS_GROUP_UNSUBSCRIBE))
        .to(GroupUnsubscribeHandler.class)
        .in(Scopes.SINGLETON);
    // PacketHandlers =========================================<<<
  }

  @Provides
  public MessageProperties messageProperties() {
    boolean autofillId =
        (boolean)
            config
                .getValue(ConfigKeys.MESSAGE_AUTOFILL_ID)
                .withFallback(ConfigValueFactory.fromAnyRef(false))
                .unwrapped();
    var m = new MessageProperties(autofillId);
    return m;
  }

  @Provides
  public Interceptors interceptors(AuthInterceptor authInterceptor) {
    return new Interceptors(authInterceptor::handle);
  }
}
