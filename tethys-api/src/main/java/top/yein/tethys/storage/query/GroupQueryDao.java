package top.yein.tethys.storage.query;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.Group;

/**
 * 群组查询数据访问接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface GroupQueryDao {

  /**
   * 查询指定群信息.
   *
   * @param id 群 ID
   * @return 群信息
   */
  Mono<Group> queryById(long id);

  /**
   * 查询指定群成员的用户 ID 列表.
   *
   * @param id 群 ID
   * @return 成员用户 ID
   */
  Flux<Long> queryMembersUid(long id);
}
