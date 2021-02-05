package top.yein.tethys.service;

import reactor.core.publisher.Flux;
import top.yein.tethys.entity.PrivateMessage;
import top.yein.tethys.query.PrivateMessageQuery;

/**
 * 私聊服务接口定义.
 *
 * @author KK (kzou227@qq.com)
 */
public interface PrivateMessageService {

  /**
   * @param query
   * @return
   */
  Flux<PrivateMessage> find(PrivateMessageQuery query);
}
