package top.yein.tethys.storage;

import static top.yein.tethys.r2dbc.Parameter.fromOrNull;

import java.util.Arrays;
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
          + " VALUES($1,$2,$3,$4,$5,$6,$7,$8,$9,now(),now())";

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
              fromOrNull(entity.getCustomArgs(), String.class)
            })
        .rowsUpdated();
  }

  @Override
  public Mono<Void> insert(Message entity, List<Long> uids) {
    if (uids == null || uids.isEmpty()) {
      throw new IllegalArgumentException("[uids]不能为NULL或者EMPTY");
    }

    var sql = new StringBuilder(128 * uids.size());
    for (Long uid : uids) {
      if (uid == null) {
        throw new IllegalArgumentException(
            "正将消息[id:" + entity.getId() + "]与NULL关联 - uids: " + Arrays.toString(uids.toArray()));
      }

      sql.append("INSERT INTO user_messages(uid,message_id,create_time) VALUES(")
          .append(uid)
          .append(",'")
          .append(entity.getId())
          .append("'")
          .append(",now());");
    }
    return Mono.zip(insert(entity), rc.batchSql(sql.toString()).rowsUpdated()).then();
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
