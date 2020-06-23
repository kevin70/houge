package io.zhudy.xim.session;

import reactor.core.publisher.Mono;

/**
 * Session 事件接口.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@FunctionalInterface
public interface SessionGroupListener {

  /**
   * @param session
   * @param event
   * @param groupId
   * @return
   */
  Mono<Void> apply(Session session, SessionGroupEvent event, String groupId);
}
