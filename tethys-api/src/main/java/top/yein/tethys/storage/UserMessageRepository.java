package top.yein.tethys.storage;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.UserMessage;

/**
 * 用户消息数据访问对象.
 *
 * @author KK (kzou227@qq.com)
 */
public interface UserMessageRepository {

  /**
   * 保存用户消息.
   *
   * @param entity 用户消息实体
   * @return 受影响行数
   */
  Mono<Integer> insert(UserMessage entity);

  /**
   * 批量保存用户消息.
   *
   * @param entities 用户消息实体列表
   * @return 受影响行数
   */
  Mono<Integer> batchInsert(List<UserMessage> entities);

  /**
   * 查询用户消息消息详细信息.
   *
   * @param uid 用户 ID
   * @return 用户消息详细信息
   */
  Flux<Void> findDetailsByUid(long uid);
}
