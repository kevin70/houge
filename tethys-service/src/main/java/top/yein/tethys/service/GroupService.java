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
package top.yein.tethys.service;

import javax.inject.Inject;
import org.roaringbitmap.longlong.Roaring64NavigableMap;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.yein.tethys.Nil;
import top.yein.tethys.storage.query.GroupQueryDao;

/**
 * 群组服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupService {

  private final GroupQueryDao groupQueryDao;

  /** 已存在的群组 ID 位图. */
  private Roaring64NavigableMap existingGidBits = Roaring64NavigableMap.bitmapOf();

  /**
   * 使用群组数据查询接口构造对象.
   *
   * @param groupQueryDao 群组数据查询接口
   */
  @Inject
  public GroupService(GroupQueryDao groupQueryDao) {
    this.groupQueryDao = groupQueryDao;
  }

  /**
   * 判断指定的群组是否存在.
   *
   * <p>如果用户存在则返回一个 {@code Mono<Nil>} 实例可用 {@code Mono} 操作符进行消费, 反之则返回 {@code Mono.empty()}.
   *
   * @param gid 群组 ID
   * @return Nil.mono()/Mono.empty()
   */
  public Mono<Nil> existsById(long gid) {
    if (existingGidBits.contains(gid)) {
      return Nil.mono();
    }

    return groupQueryDao.existsById(gid).doOnNext(unused -> updateGidBits(gid));
  }

  private void updateGidBits(long gid) {
    // existingGidBits 是非线程安全的对象
    // 将对 existingGidBits 所有的更新操作放置在同一个线程中避免额外的 Lock
    Mono.fromRunnable(() -> existingGidBits.addLong(gid))
        .subscribeOn(Schedulers.single())
        .subscribe();
  }
}
