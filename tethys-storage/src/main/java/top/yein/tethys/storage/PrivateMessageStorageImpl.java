package top.yein.tethys.storage;

import io.r2dbc.spi.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.PrivateMessage;

/**
 * 私聊消息存储.
 *
 * @author KK (kzou227@qq.com)
 */
public class PrivateMessageStorageImpl implements PrivateMessageStorage {

  private static final String STORE_SQL =
      "INSERT INTO t_private_message(id,sender_id,receiver_id,kind,content,url,custom_args)"
          + " VALUES($1,$2,$3,$4,$5,$6,$7)";
  private static final String READ_MESSAGE_SQL =
      "UPDATE t_private_message SET unread=0,update_time=now() WHERE id=:id";

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
            connection -> connection.createStatement(READ_MESSAGE_SQL).bind("id", id).execute())
        .flatMap(Result::getRowsUpdated)
        .then();
  }

  @Override
  public Flux<PrivateMessage> findByUid(int uid, int limit) {
    return null;
  }
}
