package top.yein.tethys.im.server;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.io.DataInput;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.Connection;
import reactor.netty.channel.AbortedException;
import reactor.netty.http.HttpInfos;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;
import reactor.util.context.Context;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.common.JacksonUtils;
import top.yein.tethys.core.session.DefaultSession;
import top.yein.tethys.packet.ErrorPacket;
import top.yein.tethys.packet.Packet;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionIdGenerator;
import top.yein.tethys.session.SessionManager;

/**
 * @author KK (kzou227@qq.com)
 * @date 2020-12-29 14:14
 */
@Log4j2
public class WebsocketHandler {

  private static final String BEARER_TOKEN_PREFIX = "Bearer ";
  /** 认证令牌在 query 参数中的名称. */
  private static final String ACCESS_TOKEN_QUERY_NAME = "access_token";
  /** 认证服务. */
  private final AuthService authService;
  /** 会话管理器器. */
  private final SessionManager sessionManager;
  /** 会话 ID 生成器. */
  private final SessionIdGenerator sessionIdGenerator;

  private final PacketHandler packetHandler;

  private final ObjectReader objectReader;

  public WebsocketHandler(
      AuthService authService,
      SessionManager sessionManager,
      SessionIdGenerator sessionIdGenerator,
      PacketHandler packetHandler) {
    this.authService = authService;
    this.sessionManager = sessionManager;
    this.sessionIdGenerator = sessionIdGenerator;
    this.packetHandler = packetHandler;
    //
    this.objectReader = JacksonUtils.objectMapper().readerFor(Packet.class);
  }

  //
  public Mono<Void> handle(WebsocketInbound in, WebsocketOutbound out) {
    final Connection[] connVal = new Connection[1];
    in.withConnection(conn -> connVal[0] = conn);
    final Channel channel = connVal[0].channel();

    return authService
        .authorize(getAuthorization(in))
        .flatMap(
            ac -> {
              // 将会话添加至会话管理器
              var session = new DefaultSession(sessionIdGenerator.nextId(), in, out, ac);

              log.info(
                  "channelId: {} > session add to session manager [sessionId={}, uid={}]",
                  channel.id(),
                  session.sessionId(),
                  session.uid());

              // 会话清理
              session
                  .onClose()
                  .doFinally(
                      signalType -> {
                        log.debug("会话关闭 session={}, signalType", session, signalType);
                        sessionManager.remove(session).subscribe();
                      })
                  .subscribeOn(Schedulers.boundedElastic())
                  .subscribe();
              return sessionManager.add(session).thenReturn(session);
            })
        .flatMap(
            session -> {
              receiveFrames(in, session);
              return out.neverComplete();
            });
  }

  private void receiveFrames(final WebsocketInbound in, final Session session) {
    in.aggregateFrames()
        .receiveFrames()
        .doOnError(
            e -> {
              // 异常处理
              if (AbortedException.isConnectionReset(e)) {
                return;
              }

              // 业务逻辑异常处理
              if (e instanceof BizCodeException) {
                log.debug("业务异常 session={}", session, e);
              }
            })
        .doOnTerminate(
            () -> {
              // 连接终止、清理
              if (!session.isClosed()) {
                log.debug("会话终止 session={}", session);
                session.close().subscribe();
              }
            })
        .flatMap(frame -> handleFrame(session, frame))
        .subscribe();
  }

  private Mono<Void> handleFrame(final Session session, final WebSocketFrame frame) {
    if (!(frame instanceof BinaryWebSocketFrame || frame instanceof TextWebSocketFrame)) {
      var ep = new ErrorPacket("不支持 的 ws frame 类型", "当前仅支持 binary/text frame 类型");
      return session.sendPacket(ep).then(session.close());
    }

    // 解析包内容
    final ByteBuf content = frame.content();
    final DataInput input = new ByteBufInputStream(content);
    final Packet packet;
    try {
      packet = objectReader.readValue(input);
    } catch (UnrecognizedPropertyException e) {
      var ep = new ErrorPacket("未知的属性", e.getPropertyName());
      return session.sendPacket(ep).then(session.close());
    } catch (IOException e) {
      // JSON 解析失败
      log.warn("JSON 解析失败 session={}", e);
      var ep = new ErrorPacket("解析请求包错误", null);
      return session.sendPacket(ep).then(session.close());
    }

    // 包处理
    return packetHandler
        .handle(session, packet)
        .subscriberContext(Context.of(ByteBuf.class, packet));
  }

  private String getAuthorization(WebsocketInbound in) {
    // 认证
    var bearer = in.headers().get(HttpHeaderNames.AUTHORIZATION);
    if (bearer != null) {
      if (!bearer.startsWith(BEARER_TOKEN_PREFIX)) {
        // FIXME 不支持的认证类型，响应错误信息
      }
      var token = bearer.substring(BEARER_TOKEN_PREFIX.length());
    }

    final var httpInfos = (HttpInfos) in;
    final var queryParams = new QueryStringDecoder(httpInfos.uri());
    final var params = queryParams.parameters().get(ACCESS_TOKEN_QUERY_NAME);
    if (params == null || params.isEmpty()) {
      // FIXME 缺少认证参数响应错误
      return null;
    }
    return params.get(0);
  }
}
