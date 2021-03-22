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

import static org.assertj.core.api.Assertions.assertThat;
import static top.yein.tethys.core.BizCodes.C3500;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import java.security.SecureRandom;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import reactor.test.StepVerifier;
import top.yein.tethys.auth.AuthContext;
import top.yein.tethys.core.TestUtils;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionIdGenerator;

/**
 * {@link DefaultSessionManager} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class DefaultSessionManagerTest {

  private final SessionIdGenerator sessionIdGenerator = new LocalSessionIdGenerator();

  private Cache<Long, Session> getSessions(DefaultSessionManager dsm) {
    AsyncCache<Long, Session> sessions = Whitebox.getInternalState(dsm, "sessions");
    return sessions.synchronous();
  }

  @Test
  void add() {
    var dsm = new DefaultSessionManager(sessionIdGenerator);
    var session = new TestSession();
    StepVerifier.create(dsm.add(session)).verifyComplete();

    assertThat(getSessions(dsm).getIfPresent(session.sessionId())).isEqualTo(session);
  }

  @Test
  void addConflictSessionId() {
    var dsm = new DefaultSessionManager(sessionIdGenerator);
    var session1 = new TestSession();
    var session2 =
        new TestSession() {
          @Override
          public String sessionId() {
            return session1.sessionId();
          }
        };

    var p = dsm.add(session1).then(dsm.add(session2));
    StepVerifier.create(p).expectErrorMatches(e -> TestUtils.test(e, C3500)).verify();
  }

  @Test
  void remove() {
    var dsm = new DefaultSessionManager(sessionIdGenerator);
    var session = new TestSession();
    StepVerifier.create(dsm.add(session).then(dsm.remove(session))).verifyComplete();

    assertThat(getSessions(dsm).getIfPresent(session.sessionId())).isNull();
  }

  @Test
  void removeById() {
    var dsm = new DefaultSessionManager(sessionIdGenerator);
    var session = new TestSession();
    StepVerifier.create(dsm.add(session).then(dsm.removeById(session.sessionId())))
        .expectNext(session)
        .verifyComplete();
  }

  @Test
  void removeByIdWithUid() {
    var dsm = new DefaultSessionManager(sessionIdGenerator);
    var authContext = new TestAuthContext();
    var session1 =
        new TestSession() {
          @Override
          public AuthContext authContext() {
            return authContext;
          }
        };
    var session2 =
        new TestSession() {
          @Override
          public AuthContext authContext() {
            return authContext;
          }
        };
    var p =
        dsm.add(session1)
            .then(dsm.add(session2))
            .then(dsm.removeById(session1.sessionId()))
            .then(dsm.removeById(session2.sessionId()));

    StepVerifier.create(p).expectNext(session2).verifyComplete();
  }

  @Test
  void removeByIdNoExistsSession() {
    var dsm = new DefaultSessionManager(sessionIdGenerator);
    var session = new TestSession();
    StepVerifier.create(dsm.removeById(session.sessionId())).expectComplete().verify();
  }

  @Test
  void removeByUid() {
    var dsm = new DefaultSessionManager(sessionIdGenerator);
    var uid = new SecureRandom().nextLong();
    var session =
        new TestSession() {
          @Override
          public AuthContext authContext() {
            return new AuthContext() {
              @Override
              public long uid() {
                return uid;
              }

              @Override
              public String token() {
                return "test";
              }

              @Override
              public boolean isAnonymous() {
                return false;
              }
            };
          }

          @Override
          public boolean isAnonymous() {
            return false;
          }
        };

    var p = dsm.add(session).thenMany(dsm.removeByUid(uid));
    StepVerifier.create(p).expectNext(session).expectComplete().verify();
  }

  @Test
  void removeByUidNoExistsSession() {
    var dsm = new DefaultSessionManager(sessionIdGenerator);
    var uid = new SecureRandom().nextLong();
    var p = dsm.removeByUid(uid);
    StepVerifier.create(p).expectComplete().verify();
  }

  @Test
  void all() {
    var dsm = new DefaultSessionManager(sessionIdGenerator);
    var session = new TestSession();
    var p = dsm.add(session).thenMany(dsm.all());
    StepVerifier.create(p).expectNext(session).verifyComplete();
  }
}
