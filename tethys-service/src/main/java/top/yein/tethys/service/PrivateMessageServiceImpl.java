package top.yein.tethys.service;

import reactor.core.publisher.Flux;
import top.yein.tethys.entity.PrivateMessage;
import top.yein.tethys.query.PrivateMessageQuery;
import top.yein.tethys.repository.PrivateMessageRepository;

/**
 * 私聊服务对象.
 *
 * @author KK (kzou227@qq.com)
 */
public class PrivateMessageServiceImpl implements PrivateMessageService {

  private final PrivateMessageRepository privateMessageRepository;

  /**
   * 构造函数.
   *
   * @param privateMessageRepository 私聊数据访问对象
   */
  public PrivateMessageServiceImpl(PrivateMessageRepository privateMessageRepository) {
    this.privateMessageRepository = privateMessageRepository;
  }

  /**
   * 查询.
   *
   * @param query 查询对象
   */
  @Override
  public Flux<PrivateMessage> find(PrivateMessageQuery query) {
    return privateMessageRepository.find(query);
  }
}
