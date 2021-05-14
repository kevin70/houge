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
package top.yein.tethys.ws.agent.command;

import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import top.yein.tethys.grpc.AgentPb;
import top.yein.tethys.ws.session.SessionGroupManager;
import top.yein.tethys.ws.session.SessionManager;

/**
 * 取消订阅群组消息命令处理器.
 *
 * @author KK (kzou227@qq.com)
 */
public class UnsubGroupCommandHandler implements CommandHandler {

  private static final Logger log = LogManager.getLogger();
  private final SessionManager sessionManager;
  private final SessionGroupManager sessionGroupManager;

  /**
   * 使用会话管理及会话群组管理创建对象.
   *
   * @param sessionManager 会话管理器
   * @param sessionGroupManager 会话群组管理器
   */
  @Inject
  public UnsubGroupCommandHandler(
      SessionManager sessionManager, SessionGroupManager sessionGroupManager) {
    this.sessionManager = sessionManager;
    this.sessionGroupManager = sessionGroupManager;
  }

  @Override
  public Mono<Void> handle(AgentPb.Command command) {
    if (!command.hasUnsubGroup()) {
      return Mono.empty();
    }

    var sg = command.getUnsubGroup();
    return sessionManager
        .findByUid(sg.getUid())
        .flatMap(
            session -> {
              log.debug("会话 {} 取消订阅群组消息 {}", session, sg.getGidsList());
              return sessionGroupManager.unsubGroups(session, sg.getGidsList());
            })
        .then();
  }
}
