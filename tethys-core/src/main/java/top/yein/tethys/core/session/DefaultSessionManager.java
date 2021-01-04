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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.yein.tethys.session.Session;
import top.yein.tethys.session.SessionEvent;
import top.yein.tethys.session.SessionIdGenerator;
import top.yein.tethys.session.SessionListener;
import top.yein.tethys.session.SessionManager;

/**
 * Session 管理器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class DefaultSessionManager implements SessionManager {

  // 会话监听器
  private final Set<SessionListener> sessionListeners = new LinkedHashSet<>();

  // Session 记数器
  private final Counter sessionCounter = Metrics.counter("xim.session.counter");
  // 所有的 Session
  private final AsyncCache<String, Session> sessions = Caffeine.newBuilder().buildAsync();
  // 所有用户的 Session
  private final AsyncCache<Long, Set<Session>> uidSessions = Caffeine.newBuilder().buildAsync();
  // 会话 ID 生成器
  private final SessionIdGenerator sessionIdGenerator;

  @Inject
  public DefaultSessionManager(SessionIdGenerator sessionIdGenerator) {
    this.sessionIdGenerator = sessionIdGenerator;
  }

  @Override
  public SessionIdGenerator sessionIdGenerator() {
    return sessionIdGenerator;
  }

  @Override
  public boolean registerListener(SessionListener sessionListener) {
    return sessionListeners.add(sessionListener);
  }

  @Override
  public boolean unregisterListener(SessionListener sessionListener) {
    return sessionListeners.remove(sessionListener);
  }

  @Override
  public Mono<Void> add(Session session) {
    final var p =
        Mono.defer(
            () ->
                // 将 Session 加入缓存
                Mono.fromFuture(sessions.get(session.sessionId(), k -> session))
                    .doOnNext(
                        existsSession -> {
                          if (existsSession != session) {
                            // FIXME 待修复
                            //                            throw new BizCodeException(BizCodes.C3500)
                            //                                .addContextValue("sessionId",
                            // session.sessionId());
                          }
                          sessionCounter.increment();
                        })
                    .filter(s -> !s.isAnonymous())
                    .flatMap(
                        s -> {
                          var ac = s.authContext();
                          return Mono.fromFuture(uidSessions.get(ac.uid(), k -> new HashSet<>()))
                              .doOnNext(set -> set.add(s));
                        })
                    .then()
                    // 将所有 add/remove 操作放置在同一个线程中执行， 避免使用额外的 Lock
                    .subscribeOn(Schedulers.single())
                    .publishOn(Schedulers.parallel()));

    return notify(session, SessionEvent.SM_ADD_BEFORE)
        .then(p)
        .then(notify(session, SessionEvent.SM_ADD_AFTER));
  }

  @Override
  public Mono<Void> remove(Session session) {
    final Supplier<Mono<Void>> s =
        () -> {
          sessions.synchronous().invalidate(session.sessionId());
          removeFromUidSessions(session);
          return Mono.empty();
        };

    final var p =
        Mono.defer(s)
            // 将所有 add/remove 操作放置在同一个线程中执行， 避免使用额外的 Lock
            .subscribeOn(Schedulers.single())
            .publishOn(Schedulers.parallel());

    return notify(session, SessionEvent.SM_REMOVE_BEFORE)
        .then(p)
        .then(notify(session, SessionEvent.SM_REMOVE_AFTER));
  }

  @Override
  public Mono<Session> removeById(String sessionId) {
    return findById(sessionId).flatMap(s -> this.remove(s).thenReturn(s));
  }

  @Override
  public Flux<Session> removeByUid(long uid) {
    return findByUid(uid)
        .distinct()
        .collectList()
        .filter(list -> !list.isEmpty())
        .flatMapMany(
            list -> {
              var sessionIds = new ArrayList<String>(list.size());
              for (Session session : list) {
                sessionIds.add(session.sessionId());
              }

              // 移除 uid 相关的所有会话
              sessions.synchronous().invalidateAll(sessionIds);
              uidSessions.synchronous().invalidate(list.get(0).uid());

              return Flux.fromIterable(list);
            })
        // 将所有 remove 操作放置在同一个线程中执行， 避免使用额外的 Lock
        .subscribeOn(Schedulers.single())
        .publishOn(Schedulers.parallel());
  }

  @Override
  public Mono<Session> findById(String sessionId) {
    return Mono.defer(
        () -> {
          var cf = sessions.getIfPresent(sessionId);
          if (cf == null) {
            return Mono.empty();
          }
          return Mono.fromFuture(cf);
        });
  }

  @Override
  public Flux<Session> findByUid(long uid) {
    return Flux.defer(
        () -> {
          var cf = uidSessions.getIfPresent(uid);
          if (cf == null) {
            return Flux.empty();
          }
          return Mono.fromFuture(cf).flatMapMany(Flux::fromIterable);
        });
  }

  @Override
  public Flux<Session> all() {
    return Flux.defer(() -> Flux.fromIterable(sessions.asMap().values()).flatMap(Mono::fromFuture));
  }

  private void removeFromUidSessions(Session session) {
    if (!session.isAnonymous()) {
      var syncCache = uidSessions.synchronous();
      var uid = session.uid();
      var set = syncCache.getIfPresent(uid);
      if (set != null) {
        set.remove(session);
        if (set.isEmpty()) {
          syncCache.invalidate(uid);
        }
      }
    }
  }

  private Mono<Void> notify(Session session, SessionEvent event) {
    if (this.sessionListeners.isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(this.sessionListeners)
        .flatMap(
            listener ->
                listener
                    .handle(session, event)
                    .doOnError(
                        e ->
                            // 记录监听器处理异常日志
                            log.error(
                                "监听器处理异常 [event={}, session={}, listener={}]",
                                event,
                                session,
                                listener,
                                e))
                    .onErrorResume(RuntimeException.class, e -> Mono.empty()))
        .last();
  }
}
