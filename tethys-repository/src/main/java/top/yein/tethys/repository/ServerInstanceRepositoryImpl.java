package top.yein.tethys.repository;

import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.ServerInstance;

/**
 * 服务实例数据访问仓库.
 *
 * @author KK (kzou227@qq.com)
 */
@Repository
public class ServerInstanceRepositoryImpl implements ServerInstanceRepository {

  private static final String INSERT_SQL =
      "insert into t_server_instance("
          + "id,app_name,host_name,host_address"
          + ",os_name,os_version,os_arch,os_user"
          + ",java_vm_name,java_vm_version,java_vm_vendor"
          + ",work_dir,pid,create_time,check_time)"
          + "values(:id,:appName,:hostName,:hostAddress"
          + ",:osName,:osVersion,:osArch,:osUser"
          + ",:javaVmName,:javaVmVersion,:javaVmVendor"
          + ",:workDir,:pid,now(),now())";
  private static final String UPDATE_SQL =
      "update t_server_instance "
          + "set"
          + " app_name=:appName,host_name=:hostName,host_address=:hostAddress"
          + ",os_name=:osName,os_version=:osVersion,os_arch=:osArch,os_user=:osUser"
          + ",java_vm_name=:javaVmName,java_vm_version=:javaVmVersion,java_vm_vendor=:javaVmVendor"
          + ",work_dir=:workDir,pid=:pid"
          + ",ver=ver+1,create_time=now(),check_time=now() "
          + "where id=:id and ver=:ver";
  private static final String UPDATE_CHECK_TIME_SQL =
      "update t_server_instance set check_time=now() where id=:id";
  private static final String FIND_BY_ID_SQL = "select * from t_server_instance where id=:id";

  private final DatabaseClient dc;

  /**
   * 构造函数.
   *
   * @param dc 数据库访问客户端
   */
  public ServerInstanceRepositoryImpl(DatabaseClient dc) {
    this.dc = dc;
  }

  @Override
  public Mono<Integer> insert(ServerInstance entity) {
    return dc.sql(INSERT_SQL)
        .bind("id", entity.getId())
        .bind("appName", entity.getAppName())
        .bind("hostName", entity.getHostName())
        .bind("hostAddress", entity.getHostAddress())
        .bind("osName", entity.getOsName())
        .bind("osVersion", entity.getOsVersion())
        .bind("osArch", entity.getOsArch())
        .bind("osUser", entity.getOsUser())
        .bind("javaVmName", entity.getJavaVmName())
        .bind("javaVmVersion", entity.getJavaVmVersion())
        .bind("javaVmVendor", entity.getJavaVmVendor())
        .bind("workDir", entity.getWorkDir())
        .bind("pid", entity.getPid())
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Mono<Integer> update(ServerInstance entity) {
    return dc.sql(UPDATE_SQL)
        .bind("appName", entity.getAppName())
        .bind("hostName", entity.getHostName())
        .bind("hostAddress", entity.getHostAddress())
        .bind("osName", entity.getOsName())
        .bind("osVersion", entity.getOsVersion())
        .bind("osArch", entity.getOsArch())
        .bind("osUser", entity.getOsUser())
        .bind("javaVmName", entity.getJavaVmName())
        .bind("javaVmVersion", entity.getJavaVmVersion())
        .bind("javaVmVendor", entity.getJavaVmVendor())
        .bind("workDir", entity.getWorkDir())
        .bind("pid", entity.getPid())
        .bind("id", entity.getId())
        .bind("ver", entity.getVer())
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Mono<Integer> updateCheckTime(int id) {
    return dc.sql(UPDATE_CHECK_TIME_SQL).bind("id", id).fetch().rowsUpdated();
  }

  @Override
  public Mono<ServerInstance> findById(int id) {
    return dc.sql(FIND_BY_ID_SQL).bind("id", id).map(this::mapEntity).one();
  }

  private ServerInstance mapEntity(Row row) {
    var e = new ServerInstance();
    e.setId(row.get("id", Integer.class));
    e.setAppName(row.get("app_name", String.class));
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
  }
}
