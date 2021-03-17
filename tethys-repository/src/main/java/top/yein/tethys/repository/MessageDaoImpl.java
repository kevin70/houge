package top.yein.tethys.repository;

import static top.yein.tethys.r2dbc.Parameter.fromOrNull;

import java.util.List;
import javax.inject.Inject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.Message;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * 消息数据仓库实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class MessageDaoImpl implements MessageDao {

  private static final String INSERT_SQL =
      "INSERT INTO messages("
          + "id,sender_id,receiver_id,group_id,kind,content,content_kind,url,custom_args,create_time,update_time)"
          + " VALUES($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11)";

  private final R2dbcClient rc;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public MessageDaoImpl(R2dbcClient rc) {
    this.rc = rc;
  }

  @Override
  public Mono<Integer> insert(Message entity) {
    return rc.sql(INSERT_SQL)
        .bind(
            new Object[] {
              entity.getId(),
              fromOrNull(entity.getSenderId(), Long.class),
              fromOrNull(entity.getReceiverId(), Long.class),
              fromOrNull(entity.getGroupId(), Long.class),
              fromOrNull(entity.getKind(), Integer.class),
              entity.getContent(),
              entity.getContentKind(),
              fromOrNull(entity.getUrl(), String.class),
              fromOrNull(entity.getCustomArgs(), String.class),
              entity.getCreateTime(),
              entity.getUpdateTime()
            })
        .rowsUpdated();
  }

  @Override
  public Mono<Integer> updateUnread(String id, int v) {
    return null;
  }

  @Override
  public Mono<Message> findById(String id) {
    return null;
  }

  @Override
  public Flux<Message> findByIds(List<String> ids) {
    return null;
  }
}
