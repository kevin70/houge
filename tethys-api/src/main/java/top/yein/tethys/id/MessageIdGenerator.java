package top.yein.tethys.id;

import reactor.core.publisher.Flux;

/**
 * 消息 ID 生成器.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageIdGenerator {

  /** 单次请求 ID 的最大个数. */
  int REQUEST_IDS_LIMIT = 100;

  /**
   * 生成一批 IDs.
   *
   * <p>通过 {@link Flux#limitRequest(long)} 设置请求的 ID 数量，如果请求值超过 {@link #REQUEST_IDS_LIMIT} 则返回 {@link
   * #REQUEST_IDS_LIMIT} 数量的 ID.
   *
   * @return IDs
   */
  Flux<String> nextIds();
}
