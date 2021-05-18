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
package top.yein.tethys.ws.session;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 默认会话群组管理器实现类.
 *
 * @author KK (kzou227@qq.com)
 */
public class DefaultSessionGroupManager implements SessionGroupManager {

  // 缓存组 Session
  private final AsyncCache<Long, CopyOnWriteArrayList<Session>> groupSessions =
      Caffeine.newBuilder().buildAsync();

  @Override
  public Mono<Void> subGroups(Session session, Collection<Long> groupIds) {
    if (groupIds == null || groupIds.isEmpty()) {
      return Mono.empty();
    }
    return Flux.fromIterable(groupIds)
        .flatMapSequential(
            groupId ->
                Mono.fromFuture(groupSessions.get(groupId, key -> new CopyOnWriteArrayList<>()))
                    .map(
                        sessions -> {
                          session.subGroupIds().add(groupId);
                          return sessions.addIfAbsent(session);
                        }))
        // 将所有 subGroups/unsubGroups 操作放置在同一个线程中执行，避免使用额外的 Lock
        .subscribeOn(Schedulers.single())
        .publishOn(Schedulers.parallel())
        .then();
  }

  @Override
  public Mono<Void> unsubGroups(Session session, Collection<Long> gids) {
    if (gids == null || gids.isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(gids)
        .flatMapSequential(
            gid -> {
              var f = groupSessions.getIfPresent(gid);
              if (f == null) {
                return Mono.empty();
              }
              return Mono.fromFuture(f)
                  .doOnNext(
                      set -> {
                        session.subGroupIds().remove(gid);
                        set.remove(session);
                        // 如果 group 中没有 session 则从缓存中删除
                        if (set.isEmpty()) {
                          groupSessions.synchronous().invalidate(gid);
                        }
                      });
            })
        // 将所有 subGroups/unsubGroups 操作放置在同一个线程中执行，避免使用额外的 Lock
        .subscribeOn(Schedulers.single())
        .publishOn(Schedulers.parallel())
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
}
