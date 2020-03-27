package io.zhudy.xim.session;

import reactor.core.publisher.Mono;

/**
 * Session 事件接口.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface SessionListener {

  /**
   * @param session
   * @param event
   * @return
   */
  Mono<Void> handle(Session session, SessionEvent event);
}
