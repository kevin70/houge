package top.yein.tethys.service;

import reactor.core.publisher.Mono;
import top.yein.tethys.Nil;
import top.yein.tethys.dto.GroupCreateDto;
import top.yein.tethys.vo.GroupCreateVo;

/**
 * 群组服务接口.
 *
 * @author KK (kzou227@qq.com)
 */
public interface GroupService {

  /**
   * 创建群组.
   *
   * @param creatorId 创建者用户 ID
   * @param vo VO
   * @return 群组 ID
   */
  Mono<GroupCreateDto> createGroup(long creatorId, GroupCreateVo vo);

  /**
   * 判断指定的群组是否存在.
   *
   * <p>如果用户存在则返回一个 {@code Mono<Nil>} 实例可用 {@code Mono} 操作符进行消费, 反之则返回 {@code Mono.empty()}.
   *
   * @param gid 群组 ID
   * @return Nil.mono()/Mono.empty()
   */
  Mono<Nil> existsById(long gid);
}
