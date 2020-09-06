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
package io.zhudy.xim.main;

import com.google.inject.Guice;
import com.google.inject.Stage;
import io.zhudy.xim.Env;
import io.zhudy.xim.server.ImServer;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Hooks;

/**
 * XIM 应用程序入口.
 *
 * @auth Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class Main implements Runnable {

  @Override
  public void run() {
    Hooks.onOperatorDebug();

    final var env = Env.current();
    final var stage = env == Env.PROD ? Stage.PRODUCTION : Stage.DEVELOPMENT;
    log.info("正在启动 XIM 服务, 当前 [env={}, stage={}]", env, stage);

    // 初始化 Guice
    log.info("初始化 Guice");
    final var injector = Guice.createInjector(stage, new ConfigModule(), new ImModule());
    log.info("初始化 Guice 成功");

    // 启动 IM 服务
    final var imServer = injector.getInstance(ImServer.class);
    imServer.start();

    log.info("XIM 服务启动成功");

    // 停止应用
    registerShutdownHook(
        () -> {
          log.info("XIM 服务停止中...");
          // 停止操作
          imServer.stop();
          log.info("XIM 服务停止成功");
        });
  }

  private void registerShutdownHook(final Runnable callback) {
    final var latch = new CountDownLatch(1);
    final Runnable r =
        () -> {
          try {
            callback.run();
          } catch (Throwable e) {
            log.error("XIM 服务停止失败", e);
          } finally {
            latch.countDown();
          }
        };
    Runtime.getRuntime().addShutdownHook(new Thread(r, "xim-shutdown"));

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  public static void main(String[] args) {
    new Main().run();
  }
}
