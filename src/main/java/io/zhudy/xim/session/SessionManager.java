package io.zhudy.xim.session;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 会话管理器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface SessionManager {

  /** */
  String SESSION_LISTENER_NAME_FOR_IOC = "session.listeners";

  /**
   * 添加会话.
   *
   * @param session 会话
   */
  Mono<Void> add(Session session);

  /**
   * 移除会话.
   *
   * @param session 会话
   */
  Mono<Void> remove(Session session);

  /**
   * 根据会话 ID 移除会话.
   *
   * @param sessionId 会话 ID
   * @return 已经移除的会话
   */
  Mono<Session> removeById(long sessionId);

  /**
   * 根据用户认证 ID 移除会话.
   *
   * @param uid 用户认证 ID
   * @return 已经移除的会话
   */
  Flux<Session> removeByUid(String uid);

  /**
   * 根据会话 ID 查询会话.
   *
   * @param sessionId 会话 ID
   * @return 会话
   */
  Mono<Session> findById(long sessionId);

  /**
   * 根据用户认证 ID 查询会话.
   *
   * @param uid 用户认证 ID
   * @return 用户认证 ID 会话
   */
  Flux<Session> findByUid(String uid);

  /**
   * 返回当前所有会话.
   *
   * @return 当前所有会话.
   */
  Flux<Session> all();
}
