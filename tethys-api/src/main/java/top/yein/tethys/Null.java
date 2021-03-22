package top.yein.tethys;

import reactor.core.publisher.Mono;

/**
 * Null 的工具类用于 reactor 使用.
 *
 * @author KK (kzou227@qq.com)
 */
public final class Null {

  /** */
  public static final Null INSTANCE = new Null();

  /** */
  private static final Mono<Null> MONO = Mono.just(INSTANCE);

  private Null() {
    if (INSTANCE != null) {
      throw new IllegalStateException("不允许创建 Null 实体请使用 Null.ONLY 调用");
    }
  }

  /** @return */
  public static Mono<Null> toMono() {
    return MONO;
  }
}
