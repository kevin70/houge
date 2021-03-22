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
package top.yein.tethys.storage.query;

import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import javax.inject.Inject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.domain.Paging;
import top.yein.tethys.entity.Message;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * 消息查询数据访问实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class MessageQueryDaoImpl implements MessageQueryDao {

  private static final String QUERY_BY_ID_SQL = "SELECT * FROM messages WHERE id=$1";
  private static final String QUERY_BY_USER_SQL =
      "SELECT b.* FROM"
          + " user_messages a LEFT JOIN messages b ON a.message_id = b.id"
          + " WHERE a.uid=$1 AND a.create_time>=$2"
          + " OFFSET $3 LIMIT $4";

  private final R2dbcClient rc;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public MessageQueryDaoImpl(R2dbcClient rc) {
    this.rc = rc;
  }

  @Override
  public Mono<Message> queryById(String id) {
    return rc.sql(QUERY_BY_ID_SQL).bind(0, id).map(this::mapToEntity).one();
  }

  @Override
  public Flux<Message> queryByUser(UserMessageQuery q, Paging paging) {
    return rc.sql(QUERY_BY_USER_SQL)
        .bind(new Object[] {q.getUid(), q.getBeginTime(), paging.getOffset(), paging.getLimit()})
        .map(this::mapToEntity)
        .all();
  }

  private Message mapToEntity(Row row) {
    var e = new Message();
    e.setId(row.get("id", String.class));
    e.setSenderId(row.get("sender_id", Long.class));
    e.setReceiverId(row.get("receiver_id", Long.class));
    e.setGroupId(row.get("group_id", Long.class));
    e.setKind(row.get("kind", Integer.class));
    e.setContent(row.get("content", String.class));
    e.setContentKind(row.get("content_kind", Integer.class));
    e.setUrl(row.get("url", String.class));
    e.setUnread(row.get("unread", Integer.class));
    e.setCreateTime(row.get("create_time", LocalDateTime.class));
    e.setUpdateTime(row.get("update_time", LocalDateTime.class));
    return e;
  }
}
