package io.zhudy.xim.session;

import reactor.core.publisher.Mono;

/**
 * Session 事件接口.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface SessionListener {

  /**
   * @param session 会话信息
   * @param event 事件类型
   * @return {@link Mono#empty()}
   */
  Mono<Void> handle(Session session, SessionEvent event);
}
