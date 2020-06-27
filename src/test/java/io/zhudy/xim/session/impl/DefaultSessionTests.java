/**
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
package io.zhudy.xim.session.impl;

import static org.assertj.core.api.Assertions.assertThat;

import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.packet.ErrorPacket;
import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

/** @author Kevin Zou (kevinz@weghst.com) */
class DefaultSessionTests {

  volatile static DisposableServer disposableServer;
  volatile static WebsocketInbound inbound;
  volatile static WebsocketOutbound outbound;

  Connection websocketClientConn;

  @BeforeAll
  static void beforeAll() {
    disposableServer =
        HttpServer.create()
            .port(0)
            .route(
                routes -> {
                  routes.ws(
                      "/im",
                      (inbound, outbound) -> {
                        DefaultSessionTests.inbound = inbound;
                        DefaultSessionTests.outbound = outbound;
                        return outbound.neverComplete();
                      });
                })
            .wiretap(true)
            .bindNow();
  }

  @AfterAll
  static void afterAll() {
    disposableServer.disposeNow();
  }

  @BeforeEach
  void before() {
    websocketClientConn =
        HttpClient.create()
            .baseUrl("ws://" + disposableServer.host() + ":" + disposableServer.port())
            .websocket()
            .uri("/im")
            .connect()
            .block();
  }

  @AfterEach
  void after() {
    websocketClientConn.disposeNow();
  }

  @Test
  void newSession() {
    var sessionId = new SecureRandom().nextLong();
    var authContext = AuthContext.NONE_AUTH_CONTEXT;
    var session = new DefaultSession(sessionId, inbound, outbound, authContext);

    assertThat(session)
        .hasFieldOrPropertyWithValue("sessionId", sessionId)
        .hasFieldOrPropertyWithValue("inbound", inbound)
        .hasFieldOrPropertyWithValue("outbound", outbound)
        .hasFieldOrPropertyWithValue("authContext", AuthContext.NONE_AUTH_CONTEXT);

    assertThat(session.sessionId()).as("sessionId()").isEqualTo(sessionId);
    assertThat(session.isClosed()).as("isClosed()").isEqualTo(false);
    assertThat(session.isAnonymous()).as("isAnonymous()").isEqualTo(authContext.isAnonymous());
  }

  @Test
  void sendPacket() throws InterruptedException {
    var sessionId = new SecureRandom().nextLong();
    var authContext = AuthContext.NONE_AUTH_CONTEXT;
    var session = new DefaultSession(sessionId, inbound, outbound, authContext);

    var queue = new LinkedBlockingQueue<>();
    websocketClientConn.inbound().receiveObject().doOnNext(o -> queue.offer(o)).subscribe();

    session.sendPacket(new ErrorPacket("test message", "test message")).subscribe();

    var o = queue.poll(5, TimeUnit.SECONDS);
    assertThat(o).isInstanceOf(WebSocketFrame.class);
  }

  @Test
  void closeSession() throws InterruptedException {
    var sessionId = new SecureRandom().nextLong();
    var authContext = AuthContext.NONE_AUTH_CONTEXT;
    var session = new DefaultSession(sessionId, inbound, outbound, authContext);

    // 监听会话 close 事件
    var cdl = new CountDownLatch(2);
    session.onClose().doFinally(s -> cdl.countDown()).subscribe();
    session.onClose().doFinally(s -> cdl.countDown()).subscribe();

    // 关闭会话
    session.close().then(session.close()).subscribe();
    assertThat(cdl.await(5, TimeUnit.SECONDS)).isTrue();

    assertThat(cdl.getCount()).as("onClose() count").isEqualTo(0);
    assertThat(session.isClosed()).as("isClosed()").isEqualTo(true);
    outbound.withConnection(
        connection -> {
          // 判断会话连接是否关闭
          assertThat(connection.channel().isActive()).isEqualTo(false);
        });
  }
}
