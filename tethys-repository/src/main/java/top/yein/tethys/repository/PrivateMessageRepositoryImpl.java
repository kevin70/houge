package top.yein.tethys.repository;

import io.r2dbc.spi.Result;
import java.time.LocalDateTime;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.PrivateMessage;

/**
 * 私聊消息存储.
 *
 * @author KK (kzou227@qq.com)
 */
public class PrivateMessageRepositoryImpl implements PrivateMessageRepository {

  private static final String STORE_SQL =
      "INSERT INTO t_private_message(id,sender_id,receiver_id,kind,content,url,custom_args)"
          + " VALUES($1,$2,$3,$4,$5,$6,$7)";
  private static final String READ_MESSAGE_SQL =
      "UPDATE t_private_message SET unread=0,update_time=now() WHERE id=$1";
  private static final String FIND_BY_ID_SQL = "select * from t_private_message where id=$1";
  private static final String FIND_BY_RECEIVER_ID_SQL =
      "select * from t_private_message where receiver_id=$1";

  @Override
  public Mono<Void> store(PrivateMessage entity) {
    return R2dbcUtils.getConnection()
        .flatMapMany(
            connection ->
                connection
                    .createStatement(STORE_SQL)
                    .bind("$1", entity.getId())
                    .bind("$2", entity.getSenderId())
                    .bind("$3", entity.getReceiverId())
                    .bind("$4", entity.getKind())
                    .bind("$5", entity.getContent())
                    .bind("$6", entity.getUrl())
                    .bind("$7", entity.getCustomArgs())
                    .execute())
        .flatMap(Result::getRowsUpdated)
        .then();
  }

  @Override
  public Mono<Void> readMessage(String id) {
    return R2dbcUtils.getConnection()
        .flatMapMany(
            connection -> connection.createStatement(READ_MESSAGE_SQL).bind("$1", id).execute())
        .flatMap(Result::getRowsUpdated)
        .then();
  }

  @Override
  public Flux<PrivateMessage> findById(String id) {
    return R2dbcUtils.getConnection()
        .flatMapMany(
            connection -> connection.createStatement(FIND_BY_ID_SQL).bind("$1", id).execute())
        .flatMap(this::mapEntity);
  }

  @Override
  public Flux<PrivateMessage> findByReceiverId(String receiverId, int limit) {
    return null;
  }

  private Publisher<PrivateMessage> mapEntity(Result result) {
    return result.map(
        (row, rowMetadata) -> {
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
        });
  }
}
