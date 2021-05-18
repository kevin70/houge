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
package cool.houge.ws.session;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.Metrics;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 默认会话管理器实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class DefaultSessionManager implements SessionManager {

  // Session 记数器
  private final AtomicInteger sessionCounter =
      Metrics.gauge("tethys.ws.session.counts", new AtomicInteger(0));
  // 会话缓存
  private final AsyncCache<Long, Session> sessionCache = Caffeine.newBuilder().buildAsync();
  // 用户->会话缓存
  private final AsyncCache<Long, CopyOnWriteArrayList<Session>> uidSessionCache =
      Caffeine.newBuilder().buildAsync();

  @Override
  public Mono<Void> add(Session session) {
    return Mono.fromFuture(() -> sessionCache.get(session.sessionId(), unused -> session))
        .flatMap(
            s ->
                Mono.fromFuture(
                    uidSessionCache.get(session.uid(), unused -> new CopyOnWriteArrayList<>())))
        .doOnNext(
            list -> {
              list.add(session);
              // 增加会话数量
              sessionCounter.incrementAndGet();
            })
        // 将所有add/remove操作放置在同一个线程中执行，避免使用额外的Lock
        .subscribeOn(Schedulers.single())
        .publishOn(Schedulers.parallel())
        .then();
  }

  @Override
  public Mono<Void> remove(Session session) {
    return Mono.defer(
            () -> {
              // 删除会话缓存
              sessionCache.synchronous().invalidate(session.sessionId());
              var f = uidSessionCache.getIfPresent(session.uid());
              if (f == null) {
                return Mono.empty();
              }
              return Mono.fromCompletionStage(f);
            })
        .doOnNext(
            list -> {
              // 删除用户的会话缓存
              list.remove(session);
              if (list.isEmpty()) {
                uidSessionCache.synchronous().invalidate(session.uid());
              }
              // 减少会话数量
              sessionCounter.decrementAndGet();
            })
        // 将所有add/remove操作放置在同一个线程中执行，避免使用额外的Lock
        .subscribeOn(Schedulers.single())
        .publishOn(Schedulers.parallel())
        .then();
  }

  @Override
  public Flux<Session> removeByUid(long uid) {
    return Mono.defer(
            () -> {
              var f = uidSessionCache.getIfPresent(uid);
              if (f == null) {
                return Mono.empty();
              }
              return Mono.fromFuture(f);
            })
        .doOnNext(
            list -> {
              // 删除用户的会话删除
              uidSessionCache.synchronous().invalidate(uid);
              if (!list.isEmpty()) {
                // 删除会话缓存
                sessionCache
                    .synchronous()
                    .invalidateAll(
                        list.stream().map(Session::sessionId).collect(Collectors.toList()));
                // 减少会话数量
                sessionCounter.addAndGet(-list.size());
              }
            })
        // 将所有add/remove操作放置在同一个线程中执行，避免使用额外的Lock
        .subscribeOn(Schedulers.single())
        .publishOn(Schedulers.parallel())
        .flatMapMany(Flux::fromIterable);
  }

  @Override
  public Flux<Session> findByUid(long uid) {
    return Mono.defer(
            () -> {
              var f = uidSessionCache.getIfPresent(uid);
              if (f == null) {
                return Mono.empty();
              }
              return Mono.fromCompletionStage(f);
            })
        .flatMapMany(Flux::fromIterable);
  }

  @Override
  public Flux<Session> all() {
    return Mono.fromSupplier(() -> sessionCache.asMap().values())
        .flatMapMany(Flux::fromIterable)
        .flatMap(Mono::fromCompletionStage);
  }
}
