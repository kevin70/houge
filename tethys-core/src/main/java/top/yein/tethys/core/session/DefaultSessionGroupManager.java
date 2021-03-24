/*
 * Copyright 2019-2020 the original author or authors
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
package top.yein.tethys.core.session;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionGroupEvent;
import top.yein.tethys.session.SessionGroupListener;
import top.yein.tethys.session.SessionGroupManager;

/**
 * Group Session 管理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class DefaultSessionGroupManager implements SessionGroupManager {

  private final Set<SessionGroupListener> sessionGroupListeners = new LinkedHashSet<>();
  // 缓存组 Session
  private final AsyncCache<Long, Set<Session>> groupSessions = Caffeine.newBuilder().buildAsync();

  @Override
  public boolean registerListener(SessionGroupListener sessionGroupListener) {
    return sessionGroupListeners.add(sessionGroupListener);
  }

  @Override
  public boolean unregisterListener(SessionGroupListener sessionGroupListener) {
    return sessionGroupListeners.remove(sessionGroupListener);
  }

  @Override
  public Mono<Void> subGroups(Session session, Set<Long> groupIds) {
    if (groupIds == null || groupIds.isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(groupIds)
        .flatMapSequential(
            groupId -> {
              var p =
                  Mono.fromFuture(groupSessions.get(groupId, key -> new HashSet<>()))
                      .map(
                          set -> {
                            // 添加进会话订阅组中
                            session.subGroupIds().add(groupId);
                            return set.add(session);
                          })
                      // 将所有 subGroups/unsubGroups 操作放置在同一个线程中执行，避免使用额外的 Lock
                      .subscribeOn(Schedulers.single())
                      .publishOn(Schedulers.parallel());

              return notify(session, SessionGroupEvent.GROUP_SUB_BEFORE, groupId)
                  .then(p)
                  .then(notify(session, SessionGroupEvent.GROUP_SUB_AFTER, groupId));
            })
        .then();
  }

  @Override
  public Mono<Void> unsubGroups(Session session, Set<Long> groupIds) {
    if (groupIds == null || groupIds.isEmpty()) {
      return Mono.empty();
    }

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
                            // 从会话订阅组中删除
                            session.subGroupIds().remove(groupId);

                            // 如果 group 中没有 session 则从缓存中删除
                            if (set.isEmpty()) {
                              groupSessions.synchronous().invalidate(groupId);
                            }
                          })
                      // 将所有 subGroups/unsubGroups 操作放置在同一个线程中执行，避免使用额外的 Lock
                      .subscribeOn(Schedulers.single())
                      .publishOn(Schedulers.parallel());

              return notify(session, SessionGroupEvent.GROUP_UNSUB_BEFORE, groupId)
                  .then(p)
                  .then(notify(session, SessionGroupEvent.GROUP_UNSUB_AFTER, groupId));
            })
        .then();
  }

  @Override
  public Flux<Session> findByGroupId(long groupId) {
    return Flux.defer(
        () -> {
          var cf = groupSessions.getIfPresent(groupId);
          if (cf == null) {
            return Flux.empty();
          }
          return Mono.fromFuture(cf).flatMapMany(Flux::fromIterable);
        });
  }

  private Mono<Void> notify(Session session, SessionGroupEvent event, long groupId) {
    if (this.sessionGroupListeners.isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(this.sessionGroupListeners)
        .flatMap(
            listener ->
                listener
                    .handle(session, event, groupId)
                    // 记录监听器处理异常日志
                    .doOnError(
                        e ->
                            log.error(
                                "监听器处理异常 [event={}, groupId={}, session={}, listener={}]",
                                event,
                                groupId,
                                session,
                                listener,
                                e)))
        .then();
  }
}
