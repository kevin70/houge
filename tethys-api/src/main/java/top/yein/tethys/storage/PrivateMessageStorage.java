package top.yein.tethys.storage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.PrivateMessage;

/**
 * 私聊消息存储.
 *
 * @author KK (kzou227@qq.com)
 * @date 2020-12-24 17:44
 */
public interface PrivateMessageStorage {

  /**
   * 存储私聊消息.
   *
   * @param entity 数据实体
   * @return
   */
  Mono<Void> store(PrivateMessage entity);

  /**
   * 将消息设置为已读.
   *
   * @param uid 用户 ID
   * @param messageId 消息 ID
   * @return
   */
  Mono<Void> readPrivateMessage(int uid, int messageId);

  /**
   * @param uid
   * @param limit
   * @return
   */
  Flux<PrivateMessage> findByUid(int uid, int limit);
}
