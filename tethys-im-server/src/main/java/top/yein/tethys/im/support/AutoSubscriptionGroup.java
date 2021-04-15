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

import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionEvent;
import top.yein.tethys.session.SessionGroupManager;
import top.yein.tethys.session.SessionListener;
import top.yein.tethys.session.SessionManager;
import top.yein.tethys.storage.query.GroupQueryDao;

/**
 * 自动订阅用户已关联的群组消息.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class AutoSubscriptionGroup implements SessionListener {

  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;
  private final GroupQueryDao groupQueryDao;

  /**
   * 可以被 IoC 容器使用的构造函数.
   *
   * @param sessionManager
   * @param sessionGroupManager 群组会话管理器
   * @param groupQueryDao 群组数据查询对象
   */
  @Inject
  public AutoSubscriptionGroup(
      SessionManager sessionManager,
      SessionGroupManager sessionGroupManager,
      GroupQueryDao groupQueryDao) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
    this.groupQueryDao = groupQueryDao;

    // 注册会话监听器
    this.sessionManager.registerListener(this);
  }

  @Override
  public Mono<Void> handle(Session session, SessionEvent event) {
    if (event == SessionEvent.SM_ADD_AFTER) {
      return groupQueryDao
          .queryGidByUid(session.uid())
          .collect(Collectors.toSet())
          .doOnNext(groupIds -> log.debug("用户订阅群组{}消息{}", groupIds, session))
          .flatMap(groupIds -> sessionGroupManager.subGroups(session, groupIds));
    } else if (event == SessionEvent.SM_REMOVE_AFTER) {
      return sessionGroupManager.unsubGroups(session, session.subGroupIds());
    }
    return Mono.empty();
  }
}
