package top.yein.tethys.storage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.GroupMessage;

/**
 * 群组消息存储.
 *
 * @author KK (kzou227@qq.com)
 */
public interface GroupMessageStorage {

  /**
   * 存储群组消息.
   *
   * @param entity 数据实体
   * @return RS
   */
  Mono<Void> store(GroupMessage entity);

  /**
   * 根据消息 ID 查询群组信息.
   *
   * @param id 消息 ID
   * @return 群组信息
   */
  Flux<GroupMessage> findById(String id);
}
