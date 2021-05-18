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
package cool.houge;

import reactor.core.publisher.Mono;

/**
 * 零值对象.
 *
 * <p>在响应式编程中配合 {@code Mono.empty()} 使用, 在无响应值时却需要一些消费操作时可使用 {@code Nil.mono()} 代替 {@code
 * Mono.empty}.
 *
 * <p>示例:
 *
 * <pre>{@code
 * Nil.mono().flatMap(unused-> {
 *   // 执行一些操作
 * });
 * }</pre>
 *
 * @author KK (kzou227@qq.com)
 */
public final class Nil {

  /** 全局唯一的 Nil 实例. */
  public static final Nil INSTANCE = new Nil();

  /** 默认 Nil Mono 实例. */
  private static final Mono<Nil> MONO = Mono.just(INSTANCE);

  private Nil() {}

  /**
   * 返回 Nil 的响应流发布器.
   *
   * @return 发布器.
   */
  public static Mono<Nil> mono() {
    return MONO;
  }
}
