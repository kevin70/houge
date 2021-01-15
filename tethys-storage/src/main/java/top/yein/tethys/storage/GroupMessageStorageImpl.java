package top.yein.tethys.storage;

import io.r2dbc.spi.Result;
import java.time.LocalDateTime;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.GroupMessage;

/**
 * 群聊消息存储.
 *
 * @author KK (kzou227@qq.com)
 */
public class GroupMessageStorageImpl implements GroupMessageStorage {

  private static final String STORE_SQL =
      "INSERT INTO t_group_message(id,gid,sender_id,kind,content,url,custom_args)"
          + " VALUES($1,$2,$3,$4,$5,$6,$7)";
  private static final String FIND_BY_ID_SQL = "select * from t_group_message where id=$1";

  @Override
  public Mono<Void> store(GroupMessage entity) {
    return R2dbcUtils.getConnection()
        .flatMapMany(
            connection ->
                connection
                    .createStatement(STORE_SQL)
                    .bind("$1", entity.getId())
                    .bind("$2", entity.getGid())
                    .bind("$3", entity.getSenderId())
                    .bind("$4", entity.getKind())
                    .bind("$5", entity.getContent())
                    .bind("$6", entity.getUrl())
                    .bind("$7", entity.getCustomArgs())
                    .execute())
        .flatMap(Result::getRowsUpdated)
        .then();
  }

  @Override
  public Flux<GroupMessage> findById(String id) {
    return R2dbcUtils.getConnection()
        .flatMapMany(
            connection -> connection.createStatement(FIND_BY_ID_SQL).bind("$1", id).execute())
        .flatMap(this::mapEntity);
  }

  private Publisher<GroupMessage> mapEntity(Result result) {
    return result.map(
        (row, rowMetadata) -> {
          var e = new GroupMessage();
          e.setId(row.get("id", String.class));
          e.setGid(row.get("gid", Long.class));
          e.setSenderId(row.get("sender_id", String.class));
          e.setKind(row.get("kind", Integer.class));
          e.setContent(row.get("content", String.class));
          e.setUrl(row.get("url", String.class));
          e.setCustomArgs(row.get("custom_args", String.class));
          e.setCreateTime(row.get("create_time", LocalDateTime.class));
          e.setUpdateTime(row.get("update_time", LocalDateTime.class));
          return e;
        });
  }
}
