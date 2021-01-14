package top.yein.tethys.storage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.PrivateMessage;

/**
 * 私聊消息存储.
 *
 * @author KK (kzou227@qq.com)
 */
public interface PrivateMessageStorage {

  /**
   * 存储私聊消息.
   *
   * @param entity 数据实体
   * @return RS
   */
  Mono<Void> store(PrivateMessage entity);

  /**
   * 将消息设置为已读.
   *
   * @param id 消息 ID
   * @return RS
   */
  Mono<Void> readMessage(String id);

  /**
   * 根据消息 ID 查询私聊信息.
   *
   * @param id 消息 ID
   * @return 私聊信息
   */
  Flux<PrivateMessage> findById(String id);

  /**
   * 查询用户私人聊天消息.
   *
   * @param receiverId 用户 ID
   * @param limit 返回最大记录数
   * @return 私人消息
   */
  Flux<PrivateMessage> findByReceiverId(String receiverId, int limit);
}
