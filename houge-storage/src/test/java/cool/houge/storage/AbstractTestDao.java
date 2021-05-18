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
package cool.houge.storage;

import com.typesafe.config.ConfigFactory;
import cool.houge.r2dbc.DefaultR2dbcClient;
import cool.houge.r2dbc.R2dbcClient;
import io.r2dbc.spi.ConnectionFactories;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import cool.houge.ConfigKeys;

/**
 * 测试存储的基类.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public abstract class AbstractTestDao {

  protected static R2dbcClient r2dbcClient;

  @BeforeAll
  static void setUp() {
    var config = ConfigFactory.parseResources("houge-test.conf");
    log.debug("单元测试配置\n{}", config.root().render());
    var connectionFactory =
        ConnectionFactories.get(config.getString(ConfigKeys.MESSAGE_STORAGE_R2DBC_URL));
    r2dbcClient = new DefaultR2dbcClient(connectionFactory);
  }

  /**
   * @param sql
   * @param parameters
   */
  protected void clean(String sql, Object[] parameters) {
    r2dbcClient.sql(sql).bind(parameters).rowsUpdated().block();
  }

  /**
   * @param tableName
   * @param conditions
   */
  protected void delete(String tableName, Map<String, Object> conditions) {
    var sql = new StringBuilder("DELETE FROM ").append(tableName);
    var entries = conditions.entrySet();
    if (!entries.isEmpty()) {
      sql.append(" WHERE");
      var i = 1;
      for (Entry<String, Object> e : entries) {
        sql.append(" ").append(e.getKey()).append("=$").append(i++);
      }
    }
    var spec = r2dbcClient.sql(sql.toString());
    var i = 0;
    for (Entry<String, Object> e : entries) {
      spec.bind(i++, e.getValue());
    }
    spec.rowsUpdated().block();
  }
}
