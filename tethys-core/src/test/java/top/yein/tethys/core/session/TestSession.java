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

import io.netty.buffer.ByteBuf;
import java.security.SecureRandom;
import java.util.LinkedHashSet;
import java.util.Set;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import top.yein.tethys.auth.AuthContext;
import top.yein.tethys.core.auth.NoneAuthContext;
import top.yein.tethys.session.Session;

/**
 * 测试专用的 Session.
 *
 * @author KK (kzou227@qq.com)
 */
public class TestSession implements Session {

  private final long sessionId = new SecureRandom().nextLong();
  private final Set<String> subGroupIds = new LinkedHashSet<>();

  @Override
  public String sessionId() {
    return String.valueOf(sessionId);
  }

  @Override
  public long uid() {
    if (isAnonymous()) {
      return sessionId;
    }
    return authContext().uid();
  }

  @Override
  public AuthContext authContext() {
    return NoneAuthContext.INSTANCE;
  }

  @Override
  public boolean isClosed() {
    return false;
  }

  @Override
  public Set<String> subGroupIds() {
    return subGroupIds;
  }

  @Override
  public Mono<Void> send(Publisher<ByteBuf> source) {
    return null;
  }

  @Override
  public Mono<Void> close() {
    return null;
  }

  @Override
  public Mono<Void> onClose() {
    return null;
  }
}
