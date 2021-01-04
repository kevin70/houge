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
package top.yein.tethys.core.session;

import java.time.Clock;
import java.util.concurrent.atomic.AtomicInteger;
import top.yein.tethys.session.SessionIdGenerator;

/**
 * 本地 Session ID 生成器实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class LocalSessionIdGenerator implements SessionIdGenerator {

  private final AtomicInteger seq = new AtomicInteger();

  @Override
  public String nextId() {
    seq.compareAndSet(Integer.MAX_VALUE, 0);
    long t = Clock.systemDefaultZone().instant().getEpochSecond();
    return String.valueOf((t << 32) | seq.incrementAndGet());
  }
}
