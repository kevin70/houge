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
package cool.houge.storage.impl;

import cool.houge.model.ServerInstance;
import cool.houge.r2dbc.R2dbcClient;
import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import cool.houge.storage.ServerInstanceDao;

/**
 * 服务实例数据访问仓库.
 *
 * @author KK (kzou227@qq.com)
 */
public class ServerInstanceDaoImpl implements ServerInstanceDao {

  private static final String INSERT_SQL =
      "insert into server_instances("
          + "id,app_name,host_name,host_address"
          + ",os_name,os_version,os_arch,os_user"
          + ",java_vm_name,java_vm_version,java_vm_vendor"
          + ",work_dir,pid,create_time,check_time)"
          + "values($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,now(),now())";
  private static final String DELETE_SQL = "delete from server_instances where id=$1";
  private static final String UPDATE_SQL =
      "update server_instances "
          + "set"
          + " app_name=$1,host_name=$2,host_address=$3"
          + ",os_name=$4,os_version=$5,os_arch=$6,os_user=$7"
          + ",java_vm_name=$8,java_vm_version=$9,java_vm_vendor=$10"
          + ",work_dir=$11,pid=$12"
          + ",ver=ver+1,create_time=now(),check_time=now() "
          + "where id=$13 and ver=$14";
  private static final String UPDATE_CHECK_TIME_SQL =
      "update server_instances set check_time=now() where id=$1";
  private static final String FIND_BY_ID_SQL = "select * from server_instances where id=$1";

  private final R2dbcClient rc;

  /**
   * 使用 R2DBC 客户端构造对象.
   *
   * @param rc R2DBC 客户端
   */
  @Inject
  public ServerInstanceDaoImpl(R2dbcClient rc) {
    this.rc = rc;
  }

  @Override
  public Mono<Void> insert(ServerInstance entity) {
    return rc.sql(INSERT_SQL)
        .bind(
            new Object[] {
              entity.getId(),
              entity.getAppName(),
              entity.getHostName(),
              entity.getHostAddress(),
              entity.getOsName(),
              entity.getOsVersion(),
              entity.getOsArch(),
              entity.getOsUser(),
              entity.getJavaVmName(),
              entity.getJavaVmVersion(),
              entity.getJavaVmVendor(),
              entity.getWorkDir(),
              entity.getPid()
            })
        .rowsUpdated()
        .doOnNext(
            n -> {
              if (n != 1) {
                throw new BizCodeException(BizCode.C811, "保存 server instance 失败")
                    .addContextValue("entity", entity);
              }
            })
        .then();
  }

  @Override
  public Mono<Void> delete(int id) {
    return rc.sql(DELETE_SQL)
        .bind(0, id)
        .rowsUpdated()
        .doOnNext(
            n -> {
              if (n != 1) {
                throw new BizCodeException(BizCode.C811, "删除 server instance 失败")
                    .addContextValue("id", id);
              }
            })
        .then();
  }

  @Override
  public Mono<Void> update(ServerInstance entity) {
    return rc.sql(UPDATE_SQL)
        .bind(
            new Object[] {
              entity.getAppName(),
              entity.getHostName(),
              entity.getHostAddress(),
              entity.getOsName(),
              entity.getOsVersion(),
              entity.getOsArch(),
              entity.getOsUser(),
              entity.getJavaVmName(),
              entity.getJavaVmVersion(),
              entity.getJavaVmVendor(),
              entity.getWorkDir(),
              entity.getPid(),
              entity.getId(),
              entity.getVer()
            })
        .rowsUpdated()
        .doOnNext(
            n -> {
              if (n != 1) {
                throw new BizCodeException(BizCode.C811, "更新 server instance 失败")
                    .addContextValue("entity", entity);
              }
            })
        .then();
  }

  @Override
  public Mono<Void> updateCheckTime(int id) {
    return rc.sql(UPDATE_CHECK_TIME_SQL)
        .bind(0, id)
        .rowsUpdated()
        .doOnNext(
            n -> {
              if (n != 1) {
                throw new BizCodeException(BizCode.C811, "更新 server instance `check_time` 失败")
                    .addContextValue("id", id);
              }
            })
        .then();
  }

  @Override
  public Mono<ServerInstance> findById(int id) {
    return rc.sql(FIND_BY_ID_SQL).bind(0, id).map(this::mapEntity).one();
  }

  private ServerInstance mapEntity(Row row) {
    var e = new ServerInstance();
    e.setId(row.get("id", Integer.class));
    e.setAppName(row.get("app_name", String.class));
    e.setHostName(row.get("host_name", String.class));
    e.setHostAddress(row.get("host_address", String.class));
    e.setOsName(row.get("os_name", String.class));
    e.setOsVersion(row.get("os_version", String.class));
    e.setOsArch(row.get("os_arch", String.class));
    e.setOsUser(row.get("os_user", String.class));
    e.setJavaVmName(row.get("java_vm_name", String.class));
    e.setJavaVmVersion(row.get("java_vm_version", String.class));
    e.setJavaVmVendor(row.get("java_vm_vendor", String.class));
    e.setWorkDir(row.get("work_dir", String.class));
    e.setPid(row.get("pid", Long.class));
    e.setVer(row.get("ver", Integer.class));
    e.setCreateTime(row.get("create_time", LocalDateTime.class));
    e.setCheckTime(row.get("check_time", LocalDateTime.class));
    return e;
  }
}
