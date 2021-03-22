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

import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.UidMapping;
import top.yein.tethys.r2dbc.R2dbcClient;

/**
 * {@code uid_mappings} 数据访问实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class UidMappingDaoImpl implements UidMappingDao {

  private static final String INSERT_SQL =
      "INSERT INTO uid_mappings(mapped_uid,create_time) VALUES($1,now())";
  private static final String FIND_BY_MAPPED_ID_SQL =
      "SELECT * FROM uid_mappings WHERE mapped_uid=$1";

  private final R2dbcClient rc;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public UidMappingDaoImpl(R2dbcClient rc) {
    this.rc = rc;
  }

  @Override
  public Mono<Long> insert(UidMapping entity) {
    return rc.sql(INSERT_SQL)
        .bind(0, entity.getMappedUid())
        .returnGeneratedValues("id")
        .map(row -> row.get("id", Long.class))
        .one();
  }

  @Override
  public Mono<UidMapping> findByMappedUid(String mappedUid) {
    return rc.sql(FIND_BY_MAPPED_ID_SQL).bind(0, mappedUid).map(this::mapEntity).one();
  }

  private UidMapping mapEntity(Row row) {
    var entity = new UidMapping();
    entity.setId(row.get("id", Long.class));
    entity.setMappedUid(row.get("mapped_uid", String.class));
    entity.setCreateTime(row.get("create_time", LocalDateTime.class));
    return entity;
  }
}
