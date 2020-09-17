/*
 * Copyright 2019-2020 the original author or authors
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
package io.zhudy.xim.server;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.zhudy.xim.BizCodeException;
import io.zhudy.xim.auth.AuthService;
import io.zhudy.xim.helper.PacketHelper;
import io.zhudy.xim.packet.ErrorPacket;
import io.zhudy.xim.packet.Packet;
import io.zhudy.xim.session.Session;
import io.zhudy.xim.session.SessionGroupManager;
import io.zhudy.xim.session.SessionIdGenerator;
import io.zhudy.xim.session.SessionManager;
import io.zhudy.xim.session.impl.DefaultSession;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.Connection;
import reactor.netty.channel.AbortedException;
import reactor.netty.http.HttpInfos;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;
import reactor.util.context.Context;

import javax.inject.Inject;
import java.io.DataInput;
import java.io.IOException;
import java.util.function.BiFunction;

/**
 * IM 消息处理器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class ImSocketHandler
    implements BiFunction<WebsocketInbound, WebsocketOutbound, Mono<Void>> {

  /** 会话闲置时间. */
  private static final int IDLE_TIMEOUT_SECS = 90;
  /** 认证令牌在 query 参数中的名称. */
  private static final String ACCESS_TOKEN_QUERY_NAME = "access_token";
  /** 认证服务. */
  private final AuthService authService;
  /** 会话管理器器. */
  private final SessionManager sessionManager;
  /** 会话群组管理器. */
  private final SessionGroupManager sessionGroupManager;
  /** 会话 ID 生成器. */
  private final SessionIdGenerator sessionIdGenerator;
  /** Packet 处理器. */
  private final PacketHandler packetHandler;

  /**
   * 构建聊天消息处理器.
   *
   * @param authService 认证服务
   * @param sessionManager 会话管理器
   * @param sessionGroupManager 会话群组管理器
   * @param sessionIdGenerator 会话 ID 生成器
   * @param packetHandler Packet 处理器
   */
  @Inject
  public ImSocketHandler(
      AuthService authService,
      SessionManager sessionManager,
      SessionGroupManager sessionGroupManager,
      SessionIdGenerator sessionIdGenerator,
      PacketHandler packetHandler) {
    this.authService = authService;
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
    this.sessionIdGenerator = sessionIdGenerator;
    this.packetHandler = packetHandler;
  }

  @Override
  public Mono<Void> apply(final WebsocketInbound in, final WebsocketOutbound out) {
    final Connection[] connVal = new Connection[1];
    in.withConnection(conn -> connVal[0] = conn);

    final Channel channel = connVal[0].channel();

    // 认证
    final var accessToken = getAccessToken(in);
    return authService
        .authorize(accessToken)
        .flatMap(
            authContext -> {
              // 将会话添加至会话管理器
              var session = new DefaultSession(sessionIdGenerator.nextId(), in, out, authContext);

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
                        sessionManager
                            .remove(session)
                            .then(sessionGroupManager.unsubGroups(session, session.subGroupIds()))
                            .subscribe();
                      })
                  .subscribeOn(Schedulers.boundedElastic())
                  .subscribe();
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
        .flatMap(frame -> handleFrame(session, out, frame))
        .subscribe();
  }

  private Mono<Void> handleFrame(
      final Session session, final WebsocketOutbound out, final WebSocketFrame frame) {
    if (!(frame instanceof BinaryWebSocketFrame || frame instanceof TextWebSocketFrame)) {
      var ep = new ErrorPacket("不支持 的 ws frame 类型", "当前仅支持 binary/text frame 类型");
      return session.sendPacket(ep).then(session.close());
    }

    // 解析包内容
    final ByteBuf content = frame.content();
    final DataInput input = new ByteBufInputStream(content);
    final Packet packet;
    try {
      packet = PacketHelper.MAPPER.readValue(input, Packet.class);
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
        .handle(packet, session)
        .subscriberContext(Context.of(ByteBuf.class, packet));
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
