package top.yein.tethys.service;

import reactor.core.publisher.Flux;
import top.yein.tethys.dto.GroupMessageDTO;
import top.yein.tethys.query.GroupMessageQuery;

/**
 * 群组聊天服务接口定义.
 *
 * @author KK (kzou227@qq.com)
 */
public interface GroupMessageService {

  /**
   * 查询群组消息.
   *
   * @param query 查询对象
   * @return 消息列表
   */
  Flux<GroupMessageDTO> findRecentMessages(GroupMessageQuery query);
}
