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
package top.yein.tethys.im.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionEvent;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.session.SessionManager;
import top.yein.tethys.storage.query.GroupQueryDao;

/**
 * {@link AutoSubscriptionGroup} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class AutoSubscriptionGroupTest {

  @Test
  void handle() {
    var sessionManager = mock(SessionManager.class);
    var sessionGroupManager = mock(SessionGroupManager.class);
    var groupQueryDao = mock(GroupQueryDao.class);

    when(sessionManager.registerListener(any())).thenReturn(true);

    var autoSubscriptionGroup =
        new AutoSubscriptionGroup(sessionManager, sessionGroupManager, groupQueryDao);

    for (SessionEvent e : SessionEvent.values()) {
      if (e == SessionEvent.SM_ADD_AFTER || e == SessionEvent.SM_REMOVE_BEFORE) {
        continue;
      }

      StepVerifier.create(autoSubscriptionGroup.handle(mock(Session.class), e))
          .expectComplete()
          .verify();
    }
  }

  @Test
  void handle2() {
    var uid = 99L;
    var groupIds = Set.of(1L, 2L, 3L);

    var sessionManager = mock(SessionManager.class);
    var sessionGroupManager = mock(SessionGroupManager.class);
    var groupQueryDao = mock(GroupQueryDao.class);
    var session = mock(Session.class);
    when(sessionManager.registerListener(any())).thenReturn(true);
    when(session.uid()).thenReturn(uid);
    when(groupQueryDao.queryGidByUid(uid)).thenReturn(Flux.fromIterable(groupIds));
    when(sessionGroupManager.subGroups(session, groupIds)).thenReturn(Mono.empty());

    var autoSubscriptionGroup =
        new AutoSubscriptionGroup(sessionManager, sessionGroupManager, groupQueryDao);

    StepVerifier.create(autoSubscriptionGroup.handle(session, SessionEvent.SM_ADD_AFTER))
        .expectComplete()
        .verify();

    // 校验
    verify(sessionManager).registerListener(any());
    verify(session).uid();
    verify(groupQueryDao).queryGidByUid(uid);
    verify(sessionGroupManager).subGroups(session, groupIds);
  }
}
