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
package cool.houge.ws.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.ByteString;
import com.google.protobuf.UnsafeByteOperations;
import cool.houge.ws.session.DefaultSession;
import cool.houge.ws.session.Session;
import cool.houge.ws.session.SessionGroupManager;
import cool.houge.ws.session.SessionManager;
import io.grpc.stub.StreamObserver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.Base64;
import java.util.List;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpInfos;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;
import cool.houge.grpc.AuthGrpc.AuthStub;
import cool.houge.grpc.AuthPb.AuthRequest;
import cool.houge.grpc.AuthPb.AuthResponse;
import cool.houge.grpc.PacketGrpc.PacketStub;
import cool.houge.grpc.PacketPb.PacketRequest;
import cool.houge.grpc.PacketPb.PacketResponse;
import cool.houge.grpc.SinkOneStreamObserver;
import cool.houge.grpc.UserGroupGrpc.UserGroupStub;
import cool.houge.grpc.UserGroupPb.ListGidsRequest;
import cool.houge.grpc.UserGroupPb.ListGidsResponse;
import cool.houge.util.SocketExceptionUtils;

/**
 * WebSocket消息处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class WebSocketHandler {

  private static final Logger log = LogManager.getLogger();

  /** Authentication HTTP Header 认证的 Scheme. */
  private static final String BEARER_TOKEN_PREFIX = "Bearer ";
  /** 认证令牌在 query 参数中的名称. */
  private static final String ACCESS_TOKEN_QUERY_NAME = "access_token";

  private final AuthStub authStub;
  private final PacketStub packetStub;
  private final UserGroupStub userGroupStub;
  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;

  /**
   * @param authStub
   * @param packetStub
   * @param userGroupStub
   * @param sessionManager
   * @param sessionGroupManager
   */
  @Inject
  public WebSocketHandler(
      AuthStub authStub,
      PacketStub packetStub,
      UserGroupStub userGroupStub,
      SessionManager sessionManager,
      SessionGroupManager sessionGroupManager) {
    this.authStub = authStub;
    this.packetStub = packetStub;
    this.userGroupStub = userGroupStub;
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
  }

  /**
   * @param inbound
   * @param outbound
   * @return
   */
  public Mono<Void> handle(WebsocketInbound inbound, WebsocketOutbound outbound) {
    // 1. 会话认证
    // 2. 接收WebSocket消息
    return Mono.defer(() -> authenticate(inbound, outbound))
        .flatMap(
            session -> {
              log.debug("加载用户 {} 的群组信息", session);
              return loadGids(session.uid())
                  .flatMap(gids -> sessionGroupManager.subGroups(session, gids))
                  .thenReturn(session);
            })
        .doOnSuccess(session -> receiveFrames(inbound, outbound, session))
        .then(outbound.neverComplete());
  }

  @VisibleForTesting
  void receiveFrames(WebsocketInbound inbound, WebsocketOutbound outbound, Session session) {
    sessionManager
        .add(session)
        .thenMany(inbound.aggregateFrames().receiveFrames())
        .doFinally(
            signalType -> {
              log.info("会话中止 signType={} {}", signalType, session);
              // 删除会话管理器中的 Session
              sessionManager.remove(session).subscribe();
              // 删除群组会话管理器中的 Session
              sessionGroupManager.unsubGroups(session, session.subGroupIds()).subscribe();
            })
        .doOnError(
            ex -> {
              log.error("未处理的异常 session={}", session, ex);
              outbound.sendClose().subscribe();
            })
        .subscribe(
            frame -> {
              // 处理WebSocket消息
              processPacket(frame, session);
            });
  }

  @VisibleForTesting
  void processPacket(WebSocketFrame frame, Session session) {
    var request =
        PacketRequest.newBuilder()
            .setRequestUid(session.uid())
            .setDataBytes(UnsafeByteOperations.unsafeWrap(frame.content().nioBuffer()))
            .build();
    packetStub.process(
        request,
        new StreamObserver<>() {

          @Override
          public void onNext(PacketResponse response) {
            if (response.getDataBytes() != ByteString.EMPTY) {
              Supplier<ByteBuf> s =
                  () -> Unpooled.wrappedBuffer(response.getDataBytes().asReadOnlyByteBuffer());
              session
                  .send(Mono.fromSupplier(s))
                  .doOnError(
                      ex -> {
                        if (!session.isClosed()) {
                          session.close().subscribe();
                        }
                        if (SocketExceptionUtils.ignoreLogException(ex)) {
                          log.debug("已忽略的网络异常", ex);
                          return;
                        }
                        log.error(
                            "向用户发送消息异常 session={} data(base64)={}",
                            session,
                            Base64.getEncoder().encodeToString(response.getDataBytes().toByteArray()),
                            ex);
                      })
                  .subscribe();
            }
          }

          @Override
          public void onError(Throwable t) {
            log.error(
                "远程接口处理 Packet 异常 session={} data(base64)={}",
                session,
                Base64.getEncoder().encodeToString(request.getDataBytes().toByteArray()),
                t);
          }

          @Override
          public void onCompleted() {
            // noop
          }
        });
  }

  @VisibleForTesting
  Mono<List<Long>> loadGids(long uid) {
    var request = ListGidsRequest.newBuilder().setUid(uid).build();
    var sink = new SinkOneStreamObserver<ListGidsResponse>();
    userGroupStub.listGids(request, sink);
    return sink.asMono().map(ListGidsResponse::getGidList);
  }

  @VisibleForTesting
  Mono<Session> authenticate(WebsocketInbound in, WebsocketOutbound out) {
    String token;
    try {
      token = getToken(in);
    } catch (IllegalArgumentException e) {
      log.debug("[连接关闭]-错误的认证参数 {}", in, e);
      out.sendClose(1007, e.getMessage());
      return Mono.empty();
    }

    var request = AuthRequest.newBuilder().setToken(token).build();
    var streamObserver = new SinkOneStreamObserver<AuthResponse>();
    authStub.auth(request, streamObserver);
    return streamObserver.asMono().map(resp -> new DefaultSession(in, out, resp.getUid(), token));
  }

  @VisibleForTesting
  String getToken(WebsocketInbound in) throws IllegalArgumentException {
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
}
