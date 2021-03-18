package top.yein.tethys.storage.query;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.domain.Paging;
import top.yein.tethys.entity.Message;

/**
 * 消息查询数据访问接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface MessageQueryDao {

  /**
   * 使用消息 ID 查询消息.
   *
   * @param id 消息 ID
   * @return 消息
   */
  Mono<Message> queryById(String id);

  /**
   * 分页查询符合用户指定条件的消息.
   *
   * @param q 指定的查询条件
   * @param paging 分页
   * @return
   */
  Flux<Message> queryByUser(UserMessageQuery q, Paging paging);
}
