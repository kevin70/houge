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
package io.zhudy.xim.session.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.AsyncCache;
import io.zhudy.xim.session.Session;
import io.zhudy.xim.session.SessionGroupEvent;
import io.zhudy.xim.session.SessionGroupListener;
import io.zhudy.xim.session.TestSession;
import java.util.ArrayList;
import java.util.Set;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import reactor.core.publisher.Mono;
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
    assertThat(session.subGroupIds()).containsAll(groupIds);
  }

  @Test
  void subGroupsRepeat() {
    var session = new TestSession();
    var groupIds = Set.of("group1", "group2");

    var dsgm = new DefaultSessionGroupManager();
    var p = dsgm.subGroups(session, groupIds).then(dsgm.subGroups(session, groupIds));
    StepVerifier.create(p).verifyComplete();

    // 校验 session 已经添加保存了
    AsyncCache<String, Set<Session>> groupSessions =
        Whitebox.getInternalState(dsgm, "groupSessions");
    for (String groupId : groupIds) {
      var sessions = groupSessions.synchronous().getIfPresent(groupId);
      assertThat(sessions).contains(session).hasSize(1);
    }
    assertThat(session.subGroupIds()).containsSequence(groupIds);
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
    assertThat(session.subGroupIds()).isEmpty();
  }

  @Test
  void unsubGroupsNoSession() {
    var session = new TestSession();
    var groupIds = Set.of("group1", "group2");

    var dsgm = new DefaultSessionGroupManager();
    var p = dsgm.unsubGroups(session, groupIds);
    StepVerifier.create(p).verifyComplete();
  }

  @Test
  void findByGroupId() {
    var session = new TestSession();
    var groupIds = Set.of("group1", "group2");

    var dsgm = new DefaultSessionGroupManager();
    var p = dsgm.subGroups(session, groupIds).thenMany(dsgm.findByGroupId("group1"));
    StepVerifier.create(p).expectNext(session).verifyComplete();
  }

  @Test
  void findByGroupIdNoSession() {
    var dsgm = new DefaultSessionGroupManager();
    var p = dsgm.findByGroupId("group1");
    StepVerifier.create(p).verifyComplete();
  }

  @Nested
  class SessionGroupListenerTests {

    @Test
    void groupSubEvent() {
      var gid = "group1";
      var eventValues = new ArrayList<ImmutableTriple>();
      SessionGroupListener listener =
          (session, event, groupId) -> {
            eventValues.add(ImmutableTriple.of(session, event, groupId));
            return Mono.empty();
          };

      var session = new TestSession();
      var groupIds = Set.of(gid);
      var dsgm = new DefaultSessionGroupManager(Set.of(listener));
      var p = dsgm.subGroups(session, groupIds);
      StepVerifier.create(p).verifyComplete();

      assertThat(eventValues.get(0))
          .as("group sub before")
          .hasFieldOrPropertyWithValue("left", session)
          .hasFieldOrPropertyWithValue("middle", SessionGroupEvent.GROUP_SUB_BEFORE)
          .hasFieldOrPropertyWithValue("right", gid);
      assertThat(eventValues.get(1))
          .as("group sub after")
          .hasFieldOrPropertyWithValue("left", session)
          .hasFieldOrPropertyWithValue("middle", SessionGroupEvent.GROUP_SUB_AFTER)
          .hasFieldOrPropertyWithValue("right", gid);
    }

    @Test
    void groupUnsubEvent() {
      var gid = "group1";
      var eventValues = new ArrayList<ImmutableTriple>();
      SessionGroupListener listener =
          (session, event, groupId) -> {
            if (event == SessionGroupEvent.GROUP_UNSUB_BEFORE
                || event == SessionGroupEvent.GROUP_UNSUB_AFTER) {
              eventValues.add(ImmutableTriple.of(session, event, groupId));
            }
            return Mono.empty();
          };

      var session = new TestSession();
      var groupIds = Set.of(gid);
      var dsgm = new DefaultSessionGroupManager(Set.of(listener));
      var p = dsgm.subGroups(session, groupIds).thenMany(dsgm.unsubGroups(session, groupIds));
      StepVerifier.create(p).verifyComplete();

      assertThat(eventValues.get(0))
          .as("group unsub before")
          .hasFieldOrPropertyWithValue("left", session)
          .hasFieldOrPropertyWithValue("middle", SessionGroupEvent.GROUP_UNSUB_BEFORE)
          .hasFieldOrPropertyWithValue("right", gid);
      assertThat(eventValues.get(1))
          .as("group unsub after")
          .hasFieldOrPropertyWithValue("left", session)
          .hasFieldOrPropertyWithValue("middle", SessionGroupEvent.GROUP_UNSUB_AFTER)
          .hasFieldOrPropertyWithValue("right", gid);
    }
  }
}
