package top.yein.tethys.repository;

import reactor.core.publisher.Mono;
import top.yein.tethys.entity.ServerInstance;

/**
 * 服务实例数据访问对象.
 *
 * @author KK (kzou227@qq.com)
 */
public interface ServerInstanceDao {

  /**
   * 新增应用服务实例.
   *
   * @param entity 实体
   * @return 受影响行数
   */
  Mono<Integer> insert(ServerInstance entity);

  /**
   * 删除应用服务实例.
   *
   * @param id 应用 ID
   * @return 受影响行数
   */
  Mono<Integer> delete(int id);

  /**
   * 更新应用服务实例.
   *
   * @param entity 实体
   * @return 受影响行数
   */
  Mono<Integer> update(ServerInstance entity);

  /**
   * 更新最后检查时间.
   *
   * @param id 应用 ID
   * @return 受影响行数
   */
  Mono<Integer> updateCheckTime(int id);

  /**
   * 查询应用服务实例.
   *
   * @param id 应用 ID
   * @return 实体
   */
  Mono<ServerInstance> findById(int id);
}
