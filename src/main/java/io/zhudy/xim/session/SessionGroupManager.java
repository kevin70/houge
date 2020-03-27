package io.zhudy.xim.session;

import java.util.Set;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 群组会话管理.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface SessionGroupManager {

  /** */
  String SESSION_GROUP_LISTENER_NAME_FOR_IOC = "session.group.listeners";

  /**
   * 会话订阅群组.
   *
   * @param groupIds 群组 IDs
   */
  Mono<Void> subGroups(Session session, Set<String> groupIds);

  /**
   * 会话取消订阅群组.
   *
   * @param groupIds 群组 IDs
   */
  Mono<Void> unsubGroups(Session session, Set<String> groupIds);

  /**
   * 查询群组下订阅的所有会话.
   *
   * @param groupId 群组 ID
   * @return 已订阅群组的会话
   */
  Flux<Session> findByGroupId(String groupId);
}
