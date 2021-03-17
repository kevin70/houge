package top.yein.tethys.service;

import java.time.Duration;
import java.time.LocalDateTime;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.dto.PrivateMessageDTO;
import top.yein.tethys.mapper.PrivateMessageMapper;
import top.yein.tethys.query.PrivateMessageQuery;
import top.yein.tethys.repository.PrivateMessageRepository;
import top.yein.tethys.vo.BatchReadMessageVO;

/**
 * 私聊服务对象.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class PrivateMessageServiceImpl implements PrivateMessageService {

  /**
   * 私人消息查询时间限制.
   *
   * <p>仅可查询72小时内的消息.
   */
  private static final int FIND_MESSAGE_TIME_LIMIT = 72;

  private final PrivateMessageRepository privateMessageRepository;

  /**
   * 构造函数.
   *
   * @param privateMessageRepository 私聊数据访问对象
   */
  @Inject
  public PrivateMessageServiceImpl(PrivateMessageRepository privateMessageRepository) {
    this.privateMessageRepository = privateMessageRepository;
  }

  /**
   * 查询.
   *
   * @param query 查询对象
   */
  @Override
  public Flux<PrivateMessageDTO> findRecentMessages(PrivateMessageQuery query) {
    var createTime = query.getCreateTime();
    var now = LocalDateTime.now();
    var d = Duration.between(createTime, now);
    if (d.toHours() > FIND_MESSAGE_TIME_LIMIT) {
      var newTime = now.minusHours(FIND_MESSAGE_TIME_LIMIT);
      log.warn(
          "查询[{}]私人消息时间[{}]已超过{}小时，将查询时间重置为[{}]",
          query.getReceiverId(),
          createTime,
          FIND_MESSAGE_TIME_LIMIT,
          newTime);
      query.setCreateTime(newTime);
    }
    return privateMessageRepository
        .findMessages(query)
        .map(PrivateMessageMapper.INSTANCE::toPrivateMessageDTO);
  }

  @Override
  public Mono<Void> batchReadMessage(BatchReadMessageVO vo, String receiverId) {
    return privateMessageRepository
        .batchReadMessage(vo.getMessageIds(), receiverId)
        .doOnSuccess(
            n -> log.debug("用户[{}]批量将消息{}设置为已读状态，已修改{}行", receiverId, vo.getMessageIds(), n))
        .then();
  }
}
