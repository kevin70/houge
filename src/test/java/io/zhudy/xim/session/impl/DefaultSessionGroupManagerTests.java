package io.zhudy.xim.session.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.AsyncCache;
import io.zhudy.xim.session.Session;
import io.zhudy.xim.session.TestSession;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import reactor.test.StepVerifier;

/** @author Kevin Zou (kevinz@weghst.com) */
class DefaultSessionGroupManagerTests {

  @Test
  void subGroups() {
    var session = new TestSession();
    var groupIds = Set.of("group1", "group2");

    var dsgm = new DefaultSessionGroupManager();
    var p = dsgm.subGroups(session, groupIds);
    StepVerifier.create(p).verifyComplete();

    // 校验 session 已经添加保存了
    AsyncCache<String, Set<Session>> groupSessions =
        Whitebox.getInternalState(dsgm, "groupSessions");
    for (String groupId : groupIds) {
      var sessions = groupSessions.synchronous().getIfPresent(groupId);
      assertThat(sessions).contains(session);
    }
  }

  @Test
  void unsubGroups() {
    var session = new TestSession();
    var groupIds = Set.of("group1", "group2");

    var dsgm = new DefaultSessionGroupManager();
    var p = dsgm.subGroups(session, groupIds).then(dsgm.unsubGroups(session, groupIds));
    StepVerifier.create(p).verifyComplete();

    // 校验 session 已经添加保存了
    AsyncCache<String, Set<Session>> groupSessions =
        Whitebox.getInternalState(dsgm, "groupSessions");
    for (String groupId : groupIds) {
      var sessions = groupSessions.synchronous().getIfPresent(groupId);
      assertThat(sessions).isNull();
    }
  }
}
