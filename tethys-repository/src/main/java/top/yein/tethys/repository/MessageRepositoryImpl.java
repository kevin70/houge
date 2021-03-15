package top.yein.tethys.repository;

import java.util.List;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.Parameter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.Message;

/** @author KK (kzou227@qq.com) */
public class MessageRepositoryImpl implements MessageRepository {

  private static final String INSERT_SQL =
      "INSERT INTO t_message("
          + "id,sender_id,receiver_id,group_id,kind,content,content_kind,url,custom_args,create_time,update_time)"
          + " VALUES("
          + ":id,:senderId,:receiverId,:groupId,:kind,:content,:contentKind,:url,:customArgs,:createTime,:updateTime)";

  private final DatabaseClient dc;

  /** @param dc */
  public MessageRepositoryImpl(DatabaseClient dc) {
    this.dc = dc;
  }

  @Override
  public Mono<Integer> insert(Message entity) {
    return dc.sql(INSERT_SQL)
        .bind("id", entity.getId())
        .bind("senderId", Parameter.fromOrEmpty(entity.getSenderId(), Long.class))
        .bind("receiverId", Parameter.fromOrEmpty(entity.getReceiverId(), Long.class))
        .bind("groupId", Parameter.fromOrEmpty(entity.getGroupId(), Long.class))
        .bind("kind", Parameter.fromOrEmpty(entity.getKind(), Integer.class))
        .bind("content", entity.getContent())
        .bind("contentKind", entity.getContentKind())
        .bind("url", Parameter.fromOrEmpty(entity.getUrl(), String.class))
        .bind("customArgs", Parameter.fromOrEmpty(entity.getCustomArgs(), String.class))
        .bind("createTime", entity.getCreateTime())
        .bind("updateTime", entity.getUpdateTime())
        .fetch()
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
