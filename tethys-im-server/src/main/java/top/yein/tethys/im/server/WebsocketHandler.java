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
package top.yein.tethys.im.server;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.io.DataInput;
import java.io.IOException;
import java.net.SocketException;
import java.util.function.Supplier;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.Connection;
import reactor.netty.channel.AbortedException;
import reactor.netty.http.HttpInfos;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.auth.AuthService;
import top.yein.tethys.core.session.DefaultSession;
import top.yein.tethys.packet.ErrorPacket;
import top.yein.tethys.packet.Packet;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionManager;
import top.yein.tethys.util.JsonUtils;

/**
 * Websocket 处理器.
 *
 * @author KK (kzou227@qq.com)
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

  private final PacketDispatcher packetDispatcher;

  private final ObjectReader objectReader;

  /**
   * 构造函数.
   *
   * @param authService 认证服务
   * @param sessionManager 会话管理
   * @param packetDispatcher 包分发器
   */
  @Inject
  public WebsocketHandler(
      AuthService authService, SessionManager sessionManager, PacketDispatcher packetDispatcher) {
    this.authService = authService;
    this.sessionManager = sessionManager;
    this.packetDispatcher = packetDispatcher;
    this.objectReader = JsonUtils.objectMapper().readerFor(Packet.class);
  }

  /**
   * Websocket 处理器.
   *
   * @param in 输入
   * @param out 输出
   * @return RS
   */
  public Mono<Void> handle(WebsocketInbound in, WebsocketOutbound out) {
    final Connection[] connVal = new Connection[1];
    in.withConnection(conn -> connVal[0] = conn);
    final Channel channel = connVal[0].channel();
    final Supplier<Mono<Void>> s =
        () -> {
          String accessToken;
          try {
            accessToken = getAuthorization(in);
          } catch (IllegalArgumentException e) {
            log.debug("[连接关闭] - 错误的认证参数 {} {}", in, e.getMessage());
            return out.sendClose(WebSocketCloseStatus.INVALID_PAYLOAD_DATA.code(), e.getMessage());
          }

          return authService
              .authenticate(accessToken)
              .flatMap(
                  ac -> {
                    // 将会话添加至会话管理器
                    var session =
                        new DefaultSession(
                            sessionManager.sessionIdGenerator().nextId(), in, out, ac);

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
                              log.debug("会话关闭 session={}, signalType={}", session, signalType);
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
        };
    return Mono.defer(s)
        .onErrorResume(
            e -> {
              if (ignoreException(e)) {
                log.warn("[{}] 出现异常 handle", channel.id());
                return Mono.empty();
              }
              return Mono.error(e);
            })
        .doOnError(e -> log.error("最终的未处理异常 {}", in, e));
  }

  private void receiveFrames(final WebsocketInbound in, final Session session) {
    in.aggregateFrames()
        .receiveFrames()
        .onErrorResume(
            e -> {
              // 异常处理
              if (ignoreException(e)) {
                return Mono.empty();
              }
              return Mono.error(e);
            })
        .doOnError(
            e -> {
              // 业务逻辑异常处理
              if (e instanceof BizCodeException) {
                log.debug("业务异常 session={}", session, e);
              }
            })
        .doOnTerminate(
            () -> {
              // 连接终止、清理
              if (!session.isClosed()) {
                log.info("会话终止 session={} channel=", session);

                session
                    .close()
                    .onErrorResume(
                        e -> {
                          if (ignoreException(e)) {
                            return Mono.empty();
                          }
                          log.error("服务端主动关闭会话异常 {} ", session, e);
                          return Mono.error(e);
                        })
                    .subscribe();
              }
            })
        .flatMap(frame -> handleFrame(session, frame))
        .subscribe();
  }

  private Mono<Void> handleFrame(final Session session, final WebSocketFrame frame) {
    if (!(frame instanceof BinaryWebSocketFrame || frame instanceof TextWebSocketFrame)) {
      var ep =
          ErrorPacket.builder()
              .code(BizCode.C400.getCode())
              .message("不支持 的 ws frame 类型")
              .details("当前仅支持 binary/text frame 类型")
              .build();
      return session.sendPacket(ep).then(session.close());
    }

    // 解析包内容
    final Packet packet;
    try {
      packet = objectReader.readValue((DataInput) new ByteBufInputStream(frame.content()));
    } catch (UnrecognizedPropertyException e) {
      var ep =
          ErrorPacket.builder()
              .code(BizCode.C912.getCode())
              .message("未知的属性")
              .details(e.getPropertyName())
              .build();
      return session.sendPacket(ep).then(session.close());
    } catch (InvalidTypeIdException e) {
      String message;
      if (e.getTypeId() == null) {
        message = "缺少 @ns 命名空间";
      } else {
        message = "非法的 @ns 命名空间 [" + e.getTypeId() + "]";
      }
      log.debug(message, e);
      var ep =
          ErrorPacket.builder()
              .code(BizCode.C912.getCode())
              .message(message)
              .details(e.getOriginalMessage())
              .build();
      return session.sendPacket(ep).then(session.close());
    } catch (IOException e) {
      // JSON 解析失败
      log.warn("JSON 解析失败 session={}", e);
      var ep = ErrorPacket.builder().code(BizCode.C912.getCode()).message("解析请求包错误").build();
      return session.sendPacket(ep).then(session.close());
    }

    // 包处理
    return Mono.defer(() -> packetDispatcher.dispatch(session, packet))
        .onErrorResume(
            t -> {
              if (ignoreException(t)) {
                return Mono.empty();
              }
              // 业务逻辑异常处理
              if (t instanceof BizCodeException) {
                log.debug("业务异常 session={}", session, t);
                var ex = (BizCodeException) t;
                var ep =
                    ErrorPacket.builder()
                        .code(ex.getBizCode().getCode())
                        .message(ex.getBizCode().getMessage())
                        .details(t.getMessage())
                        .build();
                return session.sendPacket(ep);
              }
              log.error("未处理的异常 session={}, packet={}", session, packet, t);
              return Mono.error(t);
            });
  }

  private String getAuthorization(WebsocketInbound in) throws IllegalArgumentException {
    var bearer = in.headers().get(HttpHeaderNames.AUTHORIZATION);
    if (bearer != null) {
      if (!bearer.startsWith(BEARER_TOKEN_PREFIX)) {
        throw new IllegalArgumentException("header认证必须使用Bearer模式");
      }
      return bearer.substring(BEARER_TOKEN_PREFIX.length());
    }

    final var httpInfos = (HttpInfos) in;
    final var queryParams = new QueryStringDecoder(httpInfos.uri());
    final var params = queryParams.parameters().get(ACCESS_TOKEN_QUERY_NAME);
    if (params == null || params.isEmpty()) {
      throw new IllegalArgumentException("QUERY中缺少\"access_token\"认证参数");
    }
    return params.get(0);
  }

  private boolean ignoreException(Throwable err) {
    return isConnectionReset(err)
        || (err instanceof AbortedException
            && ((err.getCause() != null
                    && "io.netty.channel.StacklessClosedChannelException"
                        .equals(err.getCause().getClass().getName()))
                || "Connection has been closed".equals(err.getMessage())));
  }

  private boolean isConnectionReset(Throwable err) {
    return (err instanceof IOException
            && (err.getMessage() == null
                || err.getMessage().contains("Broken pipe")
                || err.getMessage().contains("Connection reset by peer")
                || err.getMessage().contains("远程主机强迫关闭了一个现有的连接")
                || err.getMessage().contains("你的主机中的软件中止了一个已建立的连接")))
        || (err instanceof SocketException
            && err.getMessage() != null
            && err.getMessage().contains("Connection reset by peer"))
        || AbortedException.isConnectionReset(err);
  }
}
