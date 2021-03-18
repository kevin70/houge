package top.yein.tethys.service;

import java.time.Duration;
import java.time.LocalDateTime;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import top.yein.tethys.dto.GroupMessageDTO;
import top.yein.tethys.query.GroupMessageQuery;

/**
 * 群组消息服务服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class GroupMessageServiceImpl implements GroupMessageService {

  /**
   * 群组消息查询时间限制.
   *
   * <p>仅可查询72小时内的消息.
   */
  private static final int FIND_MESSAGE_TIME_LIMIT = 72;

  /**
   * 构造函数.
   *
   */
  @Inject
  public GroupMessageServiceImpl() {
  }

  @Override
  public Flux<GroupMessageDTO> findRecentMessages(GroupMessageQuery query) {
    var createTime = query.getCreateTime();
    var now = LocalDateTime.now();
    var d = Duration.between(createTime, now);
    if (d.toHours() > FIND_MESSAGE_TIME_LIMIT) {
      var newTime = now.minusHours(FIND_MESSAGE_TIME_LIMIT);
      log.warn(
          "查询[{}]组消息时间[{}]已超过{}小时，将查询时间重置为[{}]",
          query.getGroupId(),
          createTime,
          FIND_MESSAGE_TIME_LIMIT,
          newTime);
      query.setCreateTime(newTime);
    }

//    return groupMessageRepository
//        .findMessages(query)
//        .map(GroupMessageMapper.INSTANCE::toGroupMessageDTO);
    return Flux.empty();
  }
}
