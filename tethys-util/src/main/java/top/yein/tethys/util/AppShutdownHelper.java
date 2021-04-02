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
package top.yein.tethys.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import lombok.extern.log4j.Log4j2;

/**
 * 应用程序帮助类.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class AppShutdownHelper implements Runnable {

  private final Set<Runnable> callbacks = new LinkedHashSet<>();

  /**
   * 使用回调函数构建对象.
   *
   * @param callbacks 回调函数
   */
  public AppShutdownHelper(Runnable... callbacks) {
    this.callbacks.addAll(Set.of(callbacks));
  }

  /**
   * @param callback
   * @return
   */
  public AppShutdownHelper addCallback(Runnable callback) {
    this.callbacks.add(callback);
    return this;
  }

  @Override
  public void run() {
    final var latch = new CountDownLatch(1);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  var i = 0;
                  for (Runnable callback : callbacks) {
                    try {
                      i++;
                      callback.run();
                    } catch (Exception e) {
                      log.error("停止应用回调函数执行报错 [{}:{}]", i, callback, e);
                    }
                  }
                  latch.countDown();
                },
                "shutdown-helper"));
    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
