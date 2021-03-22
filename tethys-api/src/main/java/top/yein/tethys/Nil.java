package top.yein.tethys;

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

  private Nil() {
    if (INSTANCE != null) {
      throw new IllegalStateException("不允许创建 Nil 实例");
    }
  }

  /**
   * 返回 Nil 的响应流发布器.
   *
   * @return 发布器.
   */
  public static Mono<Nil> mono() {
    return MONO;
  }
}
