package io.zhudy.xim.server;

import static io.zhudy.xim.ConfigKeys.IM_SERVER_ENABLED_ANONYMOUS;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.auth.AuthService;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.packet.PacketHelper;
import io.zhudy.xim.router.PacketRouter;
import io.zhudy.xim.session.Session;
import io.zhudy.xim.session.SessionIdGenerator;
import io.zhudy.xim.session.SessionManager;
import io.zhudy.xim.session.impl.DefaultSession;
import java.io.DataInput;
import java.io.IOException;
import java.util.function.BiFunction;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.channel.AbortedException;
import reactor.netty.http.HttpInfos;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

@Log4j2
public class ImSocketHandler
    implements BiFunction<WebsocketInbound, WebsocketOutbound, Mono<Void>> {

  /** 会话闲置时间. */
  private static final int IDLE_TIMEOUT_SECS = 90;
  /** 认证令牌在 query 参数中的名称. */
  private static final String ACCESS_TOKEN_QUERY_NAME = "access_token";
  /** 是否启动匿名访问. */
  private final boolean enabledAnonymous;
  /** 认证服务. */
  private final AuthService authService;
  /** 会话管理器. */
  private final SessionManager sessionManager;
  /** 会话 ID 生成器. */
  private final SessionIdGenerator sessionIdGenerator;
  /** Packet 路由器. */
  private final PacketRouter packetRouter;

  /**
   * 构建聊天消息处理器.
   *
   * @param enabledAnonymous 是否启用匿名会话
   * @param authService 认证服务
   * @param sessionManager 会话管理
   * @param sessionIdGenerator 会话 ID 生成器
   * @param packetRouter {@link Packet} 路由器
   */
  @Inject
  public ImSocketHandler(
      @Named(IM_SERVER_ENABLED_ANONYMOUS) boolean enabledAnonymous,
      AuthService authService,
      SessionManager sessionManager,
      SessionIdGenerator sessionIdGenerator,
      PacketRouter packetRouter) {
    this.enabledAnonymous = enabledAnonymous;
    this.authService = authService;
    this.sessionManager = sessionManager;
    this.sessionIdGenerator = sessionIdGenerator;
    this.packetRouter = packetRouter;
  }

  @Override
  public Mono<Void> apply(final WebsocketInbound in, final WebsocketOutbound out) {
    final Connection[] connVal = new Connection[1];
    in.withConnection(conn -> connVal[0] = conn);

    final Channel channel = connVal[0].channel();

    // 认证
    final Mono<AuthContext> authMono;
    final var accessToken = getAccessToken(in);
    if (accessToken == null || accessToken.isBlank()) {
      // 未开启匿名认证直接关闭连接
      if (!enabledAnonymous) {
        log.debug(
            "channelId: {} > Not found \"access_token\" parameter in query string", channel.id());
        return out.sendClose(
            WebSocketCloseStatus.NORMAL_CLOSURE.code(),
            "Unauthorized - Not found \"access_token\" parameter in query string");
      } else {
        authMono = Mono.just(AuthContext.NONE_AUTH_CONTEXT);
      }
    } else {
      log.debug("channelId: {} > authenticate token {}", channel.id(), accessToken);
      authMono = authService.authorize(accessToken);
    }

    return authMono
        .flatMap(
            authContext -> {
              // 将会话添加至会话管理器
              var session = new DefaultSession(sessionIdGenerator.nextId(), in, out, authContext);
              log.info(
                  "channelId: {} > session add to session manager [sessionId={}, uid={}]",
                  channel.id(),
                  session.sessionId(),
                  session.uid());
              return sessionManager.add(session).thenReturn(session);
            })
        .flatMap(
            session -> {
              receiveFrames(in, out, session);
              return out.neverComplete();
            });
  }

  private void receiveFrames(
      final WebsocketInbound in, final WebsocketOutbound out, final Session session) {
    in.aggregateFrames()
        .receiveFrames()
        .doOnError(
            e -> {
              // 异常处理
              if (AbortedException.isConnectionReset(e)) {
                return;
              }

              System.out.println("on error");
              log.error("", e);
            })
        .doOnTerminate(
            () -> {
              // 连接终止、清理
              System.out.println("on terminate");
            })
        .doOnComplete(
            () -> {
              //
              System.out.println("on complete");
            })
        .flatMap(frame -> handleFrame(session, out, frame))
        .subscribe();
  }

  private Mono<Void> handleFrame(
      final Session session, final WebsocketOutbound out, final WebSocketFrame frame) {
    if (!(frame instanceof BinaryWebSocketFrame || frame instanceof TextWebSocketFrame)) {
      // FIXME 非 Packet 类型抛出异常
    }

    try {
      final var content = frame.content();
      final DataInput input = new ByteBufInputStream(content);
      final var packet = PacketHelper.MAPPER.readValue(input, Packet.class);
      return packetRouter
          .apply(session, Mono.just(packet))
          .onErrorResume(
              (e) -> {
                log.error("处理消息错误 -> {}", packet, e);
                return out.sendClose();
              });
    } catch (IOException e) {
      // FIXME 这里需要处理异常
      e.printStackTrace();
    }
    return Mono.empty();
  }

  private String getAccessToken(WebsocketInbound in) {
    final var httpInfos = (HttpInfos) in;
    final var queryParams = new QueryStringDecoder(httpInfos.uri());
    final var params = queryParams.parameters().get(ACCESS_TOKEN_QUERY_NAME);
    if (params == null || params.isEmpty()) {
      return null;
    }
    return params.get(0);
  }
}
