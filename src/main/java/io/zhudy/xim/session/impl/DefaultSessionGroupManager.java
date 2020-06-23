package io.zhudy.xim.session.impl;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.zhudy.xim.session.Session;
import io.zhudy.xim.session.SessionGroupEvent;
import io.zhudy.xim.session.SessionGroupListener;
import io.zhudy.xim.session.SessionGroupManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Group Session 管理器.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Log4j2
public class DefaultSessionGroupManager implements SessionGroupManager {

  // 缓存组 Session
  private final AsyncCache<String, Set<Session>> groupSessions = Caffeine.newBuilder().buildAsync();
  private final Set<SessionGroupListener> sessionGroupListeners;

  /** 创建一个没有 {@link SessionGroupEvent} 监听事件的会话组管理. */
  public DefaultSessionGroupManager() {
    this(Collections.EMPTY_SET);
  }

  /**
   * 创建一个带有 {@link SessionGroupEvent} 监听事件的会话组管理.
   *
   * @param sessionGroupListeners 监听器
   */
  @Inject
  public DefaultSessionGroupManager(
      @Named(SESSION_GROUP_LISTENER_NAME_FOR_IOC) Set<SessionGroupListener> sessionGroupListeners) {
    this.sessionGroupListeners = sessionGroupListeners;
  }

  @Override
  public Mono<Void> subGroups(Session session, Set<String> groupIds) {
    return Flux.fromIterable(groupIds)
        .flatMapSequential(
            groupId -> {
              var p =
                  Mono.fromFuture(groupSessions.get(groupId, key -> new HashSet<>()))
                      .map(set -> set.add(session));
              return notify(session, SessionGroupEvent.GROUP_SUB_BEFORE, groupId)
                  .then(p)
                  .then(notify(session, SessionGroupEvent.GROUP_SUB_AFTER, groupId));
            })
        .then()
        // 将所有 subGroups/unsubGroups 操作放置在同一个线程中执行， 避免使用额外的 Lock
        .subscribeOn(Schedulers.single())
        .publishOn(Schedulers.parallel());
  }

  @Override
  public Mono<Void> unsubGroups(Session session, Set<String> groupIds) {
    return Flux.fromIterable(groupIds)
        .flatMapSequential(
            groupId -> {
              var cf = groupSessions.getIfPresent(groupId);
              if (cf == null) {
                return Mono.empty();
              }
              var p =
                  Mono.fromFuture(cf)
                      .doOnNext(
                          set -> {
                            set.remove(session);
                            // 如果 group 中没有 session 则从缓存中删除
                            if (set.isEmpty()) {
                              groupSessions.synchronous().invalidate(groupId);
                            }
                          });

              return notify(session, SessionGroupEvent.GROUP_UNSUB_BEFORE, groupId)
                  .then(p)
                  .then(notify(session, SessionGroupEvent.GROUP_UNSUB_AFTER, groupId));
            })
        .then()
        // 将所有 subGroups/unsubGroups 操作放置在同一个线程中执行， 避免使用额外的 Lock
        .subscribeOn(Schedulers.single())
        .publishOn(Schedulers.parallel());
  }

  @Override
  public Flux<Session> findByGroupId(String groupId) {
    return Flux.defer(
        () -> {
          var cf = groupSessions.getIfPresent(groupId);
          if (cf == null) {
            return Flux.empty();
          }
          return Mono.fromFuture(cf).flatMapMany(Flux::fromIterable);
        });
  }

  private Mono<Void> notify(Session session, SessionGroupEvent event, String groupId) {
    if (this.sessionGroupListeners.isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(this.sessionGroupListeners)
        .flatMap(
            listener ->
                listener
                    .handle(session, event, groupId)
                    .doOnError(
                        e -> {
                          // 记录监听器处理异常日志
                          log.error(
                              "监听器处理异常 [event={}, groupId={}, session={}, listener={}]",
                              event,
                              groupId,
                              session,
                              listener,
                              e);
                        })
                    .onErrorResume(RuntimeException.class, e -> Mono.empty()))
        .last();
  }
}
