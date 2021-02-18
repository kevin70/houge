package top.yein.tethys.repository;

import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.Parameter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.PrivateMessage;
import top.yein.tethys.query.PrivateMessageQuery;

/**
 * 私聊消息存储.
 *
 * @author KK (kzou227@qq.com)
 */
public class PrivateMessageRepositoryImpl implements PrivateMessageRepository {

  private static final String STORE_SQL =
      "INSERT INTO t_private_message(id,sender_id,receiver_id,kind,content,url,custom_args)"
          + " VALUES(:id,:senderId,:receiverId,:kind,:content,:url,:customArgs)";
  private static final String READ_MESSAGE_SQL =
      "UPDATE t_private_message SET unread=0,update_time=now() WHERE id=:id";
  private static final String FIND_BY_ID_SQL = "select * from t_private_message where id=:id";
  private static final String FIND_SQL =
      "SELECT * FROM t_private_message"
          + " WHERE receiver_id=:receiverId AND create_time>=:createTime"
          + " ORDER BY create_time"
          + " LIMIT :limit OFFSET :offset";

  private final DatabaseClient dc;

  /**
   * 构造函数.
   *
   * @param dc 数据访问客户端
   */
  public PrivateMessageRepositoryImpl(DatabaseClient dc) {
    this.dc = dc;
  }

  @Override
  public Mono<Integer> store(PrivateMessage entity) {
    return dc.sql(STORE_SQL)
        .bind("id", entity.getId())
        .bind("senderId", entity.getSenderId())
        .bind("receiverId", entity.getReceiverId())
        .bind("kind", entity.getKind())
        .bind("content", entity.getContent())
        .bind("url", Parameter.fromOrEmpty(entity.getUrl(), String.class))
        .bind("customArgs", Parameter.fromOrEmpty(entity.getCustomArgs(), String.class))
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Mono<Integer> readMessage(String id) {
    return dc.sql(READ_MESSAGE_SQL).bind("id", id).fetch().rowsUpdated();
  }

  @Override
  public Flux<PrivateMessage> findById(String id) {
    return dc.sql(FIND_BY_ID_SQL).bind("id", id).map(this::mapEntity).all();
  }

  @Override
  public Flux<PrivateMessage> find(PrivateMessageQuery query) {
    return dc.sql(FIND_SQL)
        .bind("receiverId", query.getReceiverId())
        .bind("createTime", query.getCreateTime())
        .bind("limit", query.getLimit())
        .bind("offset", query.getOffset())
        .map(this::mapEntity)
        .all();
  }

  private PrivateMessage mapEntity(Row row) {
    var e = new PrivateMessage();
    e.setId(row.get("id", String.class));
    e.setSenderId(row.get("sender_id", String.class));
    e.setReceiverId(row.get("receiver_id", String.class));
    e.setKind(row.get("kind", Integer.class));
    e.setContent(row.get("content", String.class));
    e.setUrl(row.get("url", String.class));
    e.setCustomArgs(row.get("custom_args", String.class));
    e.setUnread(row.get("unread", Integer.class));
    e.setCreateTime(row.get("create_time", LocalDateTime.class));
    e.setUpdateTime(row.get("update_time", LocalDateTime.class));
    return e;
  }
}
