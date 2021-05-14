package top.yein.tethys.system.identifier;

import reactor.core.publisher.Mono;
import top.yein.tethys.domain.ServerInstance;

/** @author KK (kzou227@qq.com) */
public interface ServerInstanceRepository {

  /**
   * 新增应用服务实例.
   *
   * @param entity 实体
   * @return 受影响行数
   */
  Mono<Void> insert(ServerInstance entity);

  /**
   * 删除应用服务实例.
   *
   * @param id 应用 ID
   * @return 受影响行数
   */
  Mono<Void> delete(int id);

  /**
   * 更新应用服务实例.
   *
   * @param entity 实体
   * @return 受影响行数
   */
  Mono<Void> update(ServerInstance entity);

  /**
   * 更新最后检查时间.
   *
   * @param id 应用 ID
   * @return 受影响行数
   */
  Mono<Void> updateCheckTime(int id);

  /**
   * 查询应用服务实例.
   *
   * @param id 应用 ID
   * @return 实体
   */
  Mono<ServerInstance> findById(int id);
}
