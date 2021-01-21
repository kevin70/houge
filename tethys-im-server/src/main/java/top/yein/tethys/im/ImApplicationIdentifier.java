package top.yein.tethys.im;

import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.BreakException;
import top.yein.tethys.core.Version;
import top.yein.tethys.entity.ServerInstance;
import top.yein.tethys.repository.ServerInstanceRepository;
import top.yein.tethys.util.HostNameUtils;
import top.yein.tethys.util.YeinGid;

/**
 * 应用程序标识符接口.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class ImApplicationIdentifier implements ApplicationIdentifier {

  // INSTANCE 过期的时间
  private static final long INSTANCE_EXPIRES_IN = Duration.ofHours(1).toSeconds();
  // 健康检查的周期
  private static final Duration CHECK_HEALTH_PERIOD = Duration.ofMinutes(5);

  private final ServerInstanceRepository serverInstanceRepository;
  private int fid;

  /**
   * 构造函数.
   *
   * @param serverInstanceRepository
   */
  public ImApplicationIdentifier(ServerInstanceRepository serverInstanceRepository) {
    this.serverInstanceRepository = serverInstanceRepository;
    this.initFid();
  }

  @Override
  public String applicationName() {
    return "tethys-im";
  }

  @Override
  public int fid() {
    return this.fid;
  }

  @Override
  public String version() {
    return Version.version();
  }

  // 初始化 Fid
  private void initFid() {
    final var fidQueue = new ArrayBlockingQueue<Integer>(1);
    final AtomicBoolean finish = new AtomicBoolean(false);
    final SecureRandom random = new SecureRandom();
    final Function<Integer, Mono<Integer>> buildFidFunc =
        (i) -> {
          if (finish.get()) {
            return Mono.error(new BreakException("ImApplicationIdentifier - finish"));
          }

          var tempFid = random.nextInt(YeinGid.FID_MASK + 1);
          log.info("保存服务实例信息 迭代第 {} 次 fid: {}", i, tempFid);

          // fid 是否存在
          final boolean[] fidExists = {false};
          final var insertMono =
              Mono.defer(
                      () -> {
                        // 当 FID 不存在时执行插入操作
                        if (fidExists[0]) {
                          return Mono.empty();
                        }
                        return serverInstanceRepository.insert(newServerInstance(tempFid));
                      })
                  .doOnNext(
                      rowsUpdated -> {
                        if (rowsUpdated == 1) {
                          fidQueue.add(tempFid);
                          throw new BreakException("ImApplicationIdentifier - insert");
                        }
                      });

          return serverInstanceRepository
              .findById(tempFid)
              .filter(
                  si -> {
                    fidExists[0] = true;
                    // 服务实例最后检查时间与当前时间相差超过1小时则视服务实例已销毁
                    var diff = Duration.between(si.getCheckTime(), LocalDateTime.now());
                    return diff.toSeconds() > INSTANCE_EXPIRES_IN;
                  })
              .flatMap(
                  si -> {
                    log.info("发现已过期的服务实例: {}", si);

                    var entity = newServerInstance(si.getId());
                    entity.setVer(si.getVer());
                    return serverInstanceRepository.update(entity);
                  })
              .doOnNext(
                  rowsUpdated -> {
                    if (rowsUpdated == 1) {
                      fidQueue.add(tempFid);
                      throw new BreakException("ImApplicationIdentifier");
                    }
                  })
              .switchIfEmpty(insertMono);
        };

    // 迭代
    Flux.range(1, YeinGid.FID_MASK)
        .flatMapSequential(buildFidFunc)
        // 发现 BreakException 则中断执行
        .onErrorResume(BreakException.class, unused -> Mono.never())
        .onErrorContinue(
            throwable -> {
              if (!(throwable instanceof R2dbcDataIntegrityViolationException)) {
                return false;
              }
              R2dbcDataIntegrityViolationException ex =
                  (R2dbcDataIntegrityViolationException) throwable;
              // TODO: 主键冲突 - 继续执行
              return "23505".equals(ex.getSqlState());
            },
            (ex, o) -> {})
        .subscribe();

    try {
      this.fid = fidQueue.poll(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      finish.set(true);
      throw new IllegalStateException("获取服务实例 ID 失败", e);
    }
  }

  private ServerInstance newServerInstance(int id) {
    InetAddress inetAddress;
    try {
      inetAddress = HostNameUtils.getLocalHostLANAddress();
    } catch (UnknownHostException e) {
      throw new IllegalStateException(e);
    }

    var e = new ServerInstance();
    e.setId(id);
    e.setHostName(inetAddress.getHostName());
    e.setHostAddress(inetAddress.getHostAddress());
    e.setOsName(System.getProperty("os.name"));
    e.setOsVersion(System.getProperty("os.version"));
    e.setOsArch(System.getProperty("os.arch"));
    e.setOsUser(System.getProperty("user.name"));
    e.setJavaVmName(System.getProperty("java.vm.name"));
    e.setJavaVmVersion(System.getProperty("java.vm.version"));
    e.setJavaVmVendor(System.getProperty("java.vm.vendor"));
    e.setWorkDir(System.getProperty("user.dir"));
    e.setPid(ProcessHandle.current().pid());
    return e;
  }
}
