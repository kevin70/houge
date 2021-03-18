package top.yein.tethys.storage;

import java.util.List;
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
   * 保存消息并与用户进行关联.
   *
   * @param entity 消息实体
   * @param uids 关联的用户 IDs
   * @return 受影响行数
   */
  Mono<Void> insert(Message entity, List<Long> uids);

  /**
   * 更新消息 {@code unread} 列的值.
   *
   * @param id 消息 ID
   * @param v {@code unread} 的值
   * @return 受影响行数
   */
  Mono<Integer> updateUnread(String id, int v);
}
