package top.yein.tethys.repository;

import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.Parameter;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.GroupMessage;
import top.yein.tethys.query.GroupMessageQuery;

/**
 * 群聊消息存储.
 *
 * @author KK (kzou227@qq.com)
 */
@Repository
public class GroupMessageRepositoryImpl implements GroupMessageRepository {

  private static final String STORE_SQL =
      "INSERT INTO t_group_message(id,group_id,sender_id,kind,content,url,custom_args)"
          + " VALUES(:id,:groupId,:senderId,:kind,:content,:url,:customArgs)";
  private static final String FIND_BY_ID_SQL = "SELECT * FROM t_group_message WHERE id=:id";
  private static final String FIND_BY_GID_SQL =
      "SELECT * FROM t_group_message"
          + " WHERE group_id=:groupId and create_time >= :createTime"
          + " LIMIT :limit OFFSET :offset";

  private final DatabaseClient dc;

  /**
   * 构造函数.
   *
   * @param dc 数据访问客户端
   */
  public GroupMessageRepositoryImpl(DatabaseClient dc) {
    this.dc = dc;
  }

  @Override
  public Mono<Integer> store(GroupMessage entity) {
    return dc.sql(STORE_SQL)
        .bind("id", entity.getId())
        .bind("groupId", entity.getGroupId())
        .bind("senderId", entity.getSenderId())
        .bind("kind", entity.getKind())
        .bind("content", entity.getContent())
        .bind("url", Parameter.fromOrEmpty(entity.getUrl(), String.class))
        .bind("customArgs", Parameter.fromOrEmpty(entity.getCustomArgs(), String.class))
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Flux<GroupMessage> findById(String id) {
    return dc.sql(FIND_BY_ID_SQL).bind("id", id).map(this::mapEntity).all();
  }

  @Override
  public Flux<GroupMessage> findByGid(GroupMessageQuery query) {
    return dc.sql(FIND_BY_GID_SQL)
        .bind("groupId", query.getGroupId())
        .bind("createTime", query.getCreateTime())
        .bind("limit", query.getLimit())
        .bind("offset", query.getOffset())
        .map(this::mapEntity)
        .all();
  }

  private GroupMessage mapEntity(Row row) {
    var e = new GroupMessage();
    e.setId(row.get("id", String.class));
    e.setGroupId(row.get("group_id", String.class));
    e.setSenderId(row.get("sender_id", String.class));
    e.setKind(row.get("kind", Integer.class));
    e.setContent(row.get("content", String.class));
    e.setUrl(row.get("url", String.class));
    e.setCustomArgs(row.get("custom_args", String.class));
    e.setCreateTime(row.get("create_time", LocalDateTime.class));
    e.setUpdateTime(row.get("update_time", LocalDateTime.class));
    return e;
  }
}
