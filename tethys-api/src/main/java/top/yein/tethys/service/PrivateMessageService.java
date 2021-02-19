package top.yein.tethys.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.dto.PrivateMessageDTO;
import top.yein.tethys.query.PrivateMessageQuery;
import top.yein.tethys.vo.BatchReadMessageVO;

/**
 * 私聊服务接口定义.
 *
 * @author KK (kzou227@qq.com)
 */
public interface PrivateMessageService {

  /**
   * 查询最近的私人消息.
   *
   * @param query 查询条件
   * @return 信息列表
   */
  Flux<PrivateMessageDTO> findRecentMessages(PrivateMessageQuery query);

  /**
   * 批量更新消息已读状态.
   *
   * @param vo VO
   * @param receiverId 接收人 ID
   * @return RS
   */
  Mono<Void> batchReadMessage(BatchReadMessageVO vo, String receiverId);
}
