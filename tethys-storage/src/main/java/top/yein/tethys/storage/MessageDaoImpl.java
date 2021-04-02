/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.yein.tethys.storage;

import static top.yein.tethys.r2dbc.Parameter.fromOrNull;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.storage.entity.Message;
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
  private static final String UPDATE_UNREAD_STATUS_SQL =
      "UPDATE messages SET unread=$1,update_time=now() WHERE id = ANY(string_to_array($2,',')) AND receiver_id=$3";

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
    return Mono.zip(insert0(entity), rc.batchSql(sql.toString()).rowsUpdated()).then();
  }

  @Override
  public Mono<Void> updateUnreadStatus(long uid, List<String> messageIds, int v) {
    return rc.sql(UPDATE_UNREAD_STATUS_SQL)
        .bind(new Object[] {v, Joiner.on(',').join(messageIds), uid})
        .rowsUpdated()
        .then();
  }

  private Mono<Integer> insert0(Message entity) {
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
}
