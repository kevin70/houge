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
   * 事件处理.
   *
   * @param session 会话信息
   * @param event 事件类型
   * @param groupId 分组 ID
   * @return {@link Mono#empty()}
   */
  Mono<Void> apply(Session session, SessionGroupEvent event, String groupId);
}
