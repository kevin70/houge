package top.yein.tethys.repository;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.PrivateMessage;
import top.yein.tethys.query.PrivateMessageQuery;

/**
 * 私聊消息存储.
 *
 * @author KK (kzou227@qq.com)
 */
public interface PrivateMessageRepository {

  /**
   * 存储私聊消息.
   *
   * @param entity 数据实体
   * @return RS
   */
  Mono<Integer> insert(PrivateMessage entity);

  /**
   * 将消息设置为已读.
   *
   * @param id 消息 ID
   * @return RS
   */
  Mono<Integer> readMessage(String id);

  /**
   * 批量将接收者的消息设置为已读.
   *
   * @param ids 消息 IDs
   * @param receiverId 接收者
   * @return 受影响行数
   */
  Mono<Integer> batchReadMessage(List<String> ids, String receiverId);

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
   * @param query 查询对象
   * @return 私人消息
   */
  Flux<PrivateMessage> findMessages(PrivateMessageQuery query);
}
