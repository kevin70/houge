package top.yein.tethys.storage;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.Message;

/**
 * 消息数据访问实体.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageDao {

  /**
   * 保存消息.
   *
   * @param entity 消息实体
   * @return 受影响行数
   */
  Mono<Integer> insert(Message entity);

  /**
   * 更新消息 {@code unread} 列的值.
   *
   * @param id 消息 ID
   * @param v {@code unread} 的值
   * @return 受影响行数
   */
  Mono<Integer> updateUnread(String id, int v);

  /**
   * 使用消息 ID 查询消息.
   *
   * @param id 消息 ID
   * @return 消息实体
   */
  Mono<Message> findById(String id);

  /**
   * 使用消息 ID 列表批量查询消息.
   *
   * @param ids 消息 ID 列表
   * @return 消息实体
   */
  Flux<Message> findByIds(List<String> ids);
}
