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
package top.yein.tethys.r2dbc;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.r2dbc.spi.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import reactor.core.publisher.Mono;
import top.yein.tethys.r2dbc.R2dbcClient.ExecuteSpec;
import top.yein.tethys.r2dbc.R2dbcClient.FetchSpec;

/**
 * 默认实现.
 *
 * @author KK (kzou227@qq.com)
 */
class DefaultExecuteSpec implements ExecuteSpec {

  final ConnectionAccessor connectionAccessor;
  final String sql;
  final Map<Integer, Parameter> parameters;
  String[] returnGeneratedColumns;

  DefaultExecuteSpec(ConnectionAccessor connectionAccessor, String sql) {
    this.connectionAccessor = connectionAccessor;
    this.sql = sql;
    this.parameters = new LinkedHashMap<>();
  }

  @Override
  public ExecuteSpec bind(int index, Object value) {
    Objects.requireNonNull(value, "[value]不能为 [wNULL");
    if (value instanceof Parameter) {
      parameters.put(index, (Parameter) value);
    } else {
      parameters.put(index, Parameter.from(value));
    }
    return this;
  }

  @Override
  public ExecuteSpec bind(int index, Object value, Class<?> type) {
    Objects.requireNonNull(type, "[type]不能为 NULL");
    if (value instanceof Parameter) {
      parameters.put(index, (Parameter) value);
    } else {
      parameters.put(index, Parameter.fromOrNull(value, type));
    }
    return this;
  }

  @Override
  public ExecuteSpec bind(Object[] parameters) {
    Objects.requireNonNull(parameters, "[parameters]不能为 NULL");
    for (int i = 0; i < parameters.length; i++) {
      var v = parameters[i];
      if (v == null) {
        throw new NullPointerException("[parameters]参数的第[" + i + "]索引的元素为 NULL");
      }
      bind(i, v);
    }
    return this;
  }

  @Override
  public ExecuteSpec returnGeneratedValues(String... columns) {
    this.returnGeneratedColumns = columns;
    return this;
  }

  @Override
  public <R> FetchSpec<R> map(Function<Row, R> mappingFunction) {
    return this.map((row, rowMetadata) -> mappingFunction.apply(row));
  }

  @Override
  public <R> FetchSpec<R> map(BiFunction<Row, RowMetadata, R> mappingFunction) {
    return new DefaultFetchSpec<>(
        this.sql,
        this.connectionAccessor,
        this::statementFunction,
        result -> result.map(mappingFunction));
  }

  @Override
  public FetchSpec<Map<String, Object>> fetch() {
    return this.map(
        (row, rowMetadata) -> {
          var map = new LinkedHashMap<String, Object>();
          for (String columnName : rowMetadata.getColumnNames()) {
            map.put(columnName, row.get(columnName));
          }
          return map;
        });
  }

  @Override
  public Mono<Integer> rowsUpdated() {
    //    return new DefaultFetchSpec<>(
    //            this.sql, this.connectionAccessor, this::statementFunction,
    // Result::getRowsUpdated)
    //        .all()
    //        .last();
    return new DefaultFetchSpec<>(this.sql, this.connectionAccessor, this::statementFunction, null)
        .rowsUpdated();
  }

  private void rowsUpdatedFunction(Connection connection) {
    Mono.from(connection.createStatement("").execute())
        .flatMap(result -> Mono.from(result.getRowsUpdated()));
  }

  private Statement statementFunction(Connection connection) {
    var statement = connection.createStatement(sql);
    for (Entry<Integer, Parameter> entry : parameters.entrySet()) {
      var i = entry.getKey();
      var v = entry.getValue();
      if (v.isNull()) {
        statement.bindNull(i, v.type());
      } else {
        statement.bind(i, v.value());
      }
    }
    if (returnGeneratedColumns != null) {
      statement.returnGeneratedValues(returnGeneratedColumns);
    }
    return statement;
  }
}
