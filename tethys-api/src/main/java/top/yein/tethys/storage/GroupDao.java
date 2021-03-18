package top.yein.tethys.storage;

import reactor.core.publisher.Mono;
import top.yein.tethys.entity.Group;

/**
 * 群组数据访问接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface GroupDao {

  /**
   * 保存群组信息.
   *
   * @param entity 群实体
   * @return 受影响行数
   */
  Mono<Integer> insert(Group entity);

  /**
   * 在指定的群中将指定用户设置为群成员.
   *
   * @param gid 群组 ID
   * @param uid 用户 ID
   * @return 受影响行数
   */
  Mono<Integer> joinMember(long gid, long uid);

  /**
   * 将指定的群组中的指定用户移除.
   *
   * @param gid 群组 ID
   * @param uid 用户 ID
   * @return 受影响行数
   */
  Mono<Integer> removeMember(long gid, long uid);
}
