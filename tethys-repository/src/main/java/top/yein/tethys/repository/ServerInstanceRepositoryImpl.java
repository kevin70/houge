package top.yein.tethys.repository;

import io.r2dbc.spi.Result;
import java.time.LocalDateTime;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.ServerInstance;

/** @author KK (kzou227@qq.com) */
public class ServerInstanceRepositoryImpl implements ServerInstanceRepository {

  private static final String INSERT_SQL =
      "insert into t_server_instance("
          + "id,host_name,host_address"
          + ",os_name,os_version,os_arch,os_user"
          + ",java_vm_name,java_vm_version,java_vm_vendor"
          + ",work_dir,pid"
          + ",create_time,check_time)"
          + " values($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,now(),now())";
  private static final String UPDATE_SQL =
      "update t_server_instance "
          + "set"
          + " host_name=$1,host_address=$2"
          + ",os_name=$3,os_version=$4,os_arch=$5,os_user=$6"
          + ",java_vm_name=$7,java_vm_version=$8,java_vm_vendor=$9"
          + ",work_dir=$10,pid=$11"
          + ",ver=ver+1,create_time=now(),check_time=now() "
          + "where id=$12 and ver=$13";
  private static final String UPDATE_CHECK_TIME_SQL =
      "update t_server_instance set check_time=now() where id=$1";
  private static final String FIND_BY_ID_SQL = "select * from t_server_instance where id=$1";

  /**
   * @param entity
   * @return
   */
  public Mono<Integer> insert(ServerInstance entity) {
    return R2dbcUtils.getConnection()
        .flatMapMany(
            connection ->
                Flux.from(
                        connection
                            .createStatement(INSERT_SQL)
                            .bind("$1", entity.getId())
                            .bind("$2", entity.getHostName())
                            .bind("$3", entity.getHostAddress())
                            .bind("$4", entity.getOsName())
                            .bind("$5", entity.getOsVersion())
                            .bind("$6", entity.getOsArch())
                            .bind("$7", entity.getOsUser())
                            .bind("$8", entity.getJavaVmName())
                            .bind("$9", entity.getJavaVmVersion())
                            .bind("$10", entity.getJavaVmVendor())
                            .bind("$11", entity.getWorkDir())
                            .bind("$12", entity.getPid())
                            .execute())
                    .flatMap(rs -> rs.getRowsUpdated()))
        .next();
  }

  /**
   * @param entity
   * @return
   */
  public Mono<Integer> update(ServerInstance entity) {
    return R2dbcUtils.getConnection()
        .flatMapMany(
            connection ->
                Flux.from(
                        connection
                            .createStatement(UPDATE_SQL)
                            .bind("$1", entity.getHostName())
                            .bind("$2", entity.getHostAddress())
                            .bind("$3", entity.getOsName())
                            .bind("$4", entity.getOsVersion())
                            .bind("$5", entity.getOsArch())
                            .bind("$6", entity.getOsUser())
                            .bind("$7", entity.getJavaVmName())
                            .bind("$8", entity.getJavaVmVersion())
                            .bind("$9", entity.getJavaVmVendor())
                            .bind("$10", entity.getWorkDir())
                            .bind("$11", entity.getPid())
                            .bind("$12", entity.getId())
                            .bind("13", entity.getVer())
                            .execute())
                    .flatMap(rs -> rs.getRowsUpdated()))
        .next();
  }

  /**
   * @param id
   * @return
   */
  public Mono<Integer> updateCheckTime(int id) {
    return R2dbcUtils.getConnection()
        .flatMapMany(
            connection ->
                Flux.from(
                        connection.createStatement(UPDATE_CHECK_TIME_SQL).bind("$1", id).execute())
                    .flatMap(rs -> rs.getRowsUpdated()))
        .next();
  }

  /**
   * @param id 实例 ID
   * @return
   */
  public Mono<ServerInstance> findById(int id) {
    return R2dbcUtils.getConnection()
        .flatMapMany(
            connection ->
                Mono.from(connection.createStatement(FIND_BY_ID_SQL).bind("$1", id).execute())
                    .flatMap(this::mapEntity))
        .next();
  }

  private Mono<ServerInstance> mapEntity(Result rs) {
    return Mono.from(
        rs.map(
            (row, rowMetadata) -> {
              var e = new ServerInstance();
              e.setId(row.get("id", Integer.class));
              e.setHostName(row.get("host_name", String.class));
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
            }));
  }
}
