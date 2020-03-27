package io.zhudy.xim.session.impl;

import static io.zhudy.xim.BizCodes.C3500;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import io.zhudy.xim.TestUtils;
import io.zhudy.xim.auth.AuthContext;
import io.zhudy.xim.session.Session;
import io.zhudy.xim.session.TestAuthContext;
import io.zhudy.xim.session.TestSession;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import reactor.test.StepVerifier;

/** @author Kevin Zou (kevinz@weghst.com) */
class DefaultSessionManagerTests {

  private Cache<Long, Session> getSessions(DefaultSessionManager dsm) {
    AsyncCache<Long, Session> sessions = Whitebox.getInternalState(dsm, "sessions");
    return sessions.synchronous();
  }

  @Test
  void add() {
    var dsm = new DefaultSessionManager();
    var session = new TestSession();
    StepVerifier.create(dsm.add(session)).verifyComplete();

    assertThat(getSessions(dsm).getIfPresent(session.sessionId())).isEqualTo(session);
  }

  @Test
  void addConflictSessionId() {
    var dsm = new DefaultSessionManager();
    var session1 = new TestSession();
    var session2 =
        new TestSession() {
          @Override
          public long sessionId() {
            return session1.sessionId();
          }
        };

    var p = dsm.add(session1).then(dsm.add(session2));
    StepVerifier.create(p).expectErrorMatches(e -> TestUtils.test(e, C3500)).verify();
  }

  @Test
  void remove() {
    var dsm = new DefaultSessionManager();
    var session = new TestSession();
    StepVerifier.create(dsm.add(session).then(dsm.remove(session))).verifyComplete();

    assertThat(getSessions(dsm).getIfPresent(session.sessionId())).isNull();
  }

  @Test
  void removeById() {
    var dsm = new DefaultSessionManager();
    var session = new TestSession();
    StepVerifier.create(dsm.add(session).then(dsm.removeById(session.sessionId())))
        .expectNext(session)
        .verifyComplete();
  }

  @Test
  void removeByIdWithUid() {
    var dsm = new DefaultSessionManager();
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
    var dsm = new DefaultSessionManager();
    var session = new TestSession();
    StepVerifier.create(dsm.removeById(session.sessionId())).expectComplete().verify();
  }

  @Test
  void removeByUid() {
    var dsm = new DefaultSessionManager();
    var uid = Long.toHexString(ThreadLocalRandom.current().nextLong());
    var session =
        new TestSession() {
          @Override
          public AuthContext authContext() {
            return new AuthContext() {
              @Override
              public String uid() {
                return uid;
              }

              @Nonnull
              @Override
              public String token() {
                return null;
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
    var dsm = new DefaultSessionManager();
    var uid = Long.toHexString(ThreadLocalRandom.current().nextLong());
    var p = dsm.removeByUid(uid);
    StepVerifier.create(p).expectComplete().verify();
  }

  @Test
  void all() {
    var dsm = new DefaultSessionManager();
    var session = new TestSession();
    var p = dsm.add(session).thenMany(dsm.all());
    StepVerifier.create(p).expectNext(session).verifyComplete();
  }
}
