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
package cool.houge.system.identifier;

import cool.houge.model.ServerInstance;
import cool.houge.util.HostNameUtils;
import cool.houge.util.YeinGid;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import cool.houge.Version;

/**
 * 抽象应用 ID 实现.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public abstract class AbstractApplicationIdentifier implements ApplicationIdentifier {

  /** 随机生成的 FID 最小值. */
  static final int MIN_FID = 99;
  /** 随机生成的 FID 最大值. */
  static final int MAX_FID = YeinGid.FID_MASK;
  /** 构建 FID 的超时时间（秒）. */
  private static final long MAKE_FID_TIMEOUT = 10;

  // INSTANCE 过期的时间
  private static final long INSTANCE_EXPIRES_IN = Duration.ofHours(1).toSeconds();
  // 健康检查的周期
  private static final Duration CHECK_HEALTH_PERIOD = Duration.ofMinutes(1);

  private final ServerInstanceRepository serverInstanceRepository;
  private final int fid;

  /**
   * 使用服务实例数据访问对象构造对象.
   *
   * @param serverInstanceRepository 服务实例数据访问对象
   */
  protected AbstractApplicationIdentifier(ServerInstanceRepository serverInstanceRepository) {
    this.serverInstanceRepository = serverInstanceRepository;
    this.fid = initFid();
    this.checkHealth();
  }

  @Override
  public int fid() {
    return this.fid;
  }

  @Override
  public String version() {
    return Version.version();
  }

  @Override
  public void clean() {
    var future = serverInstanceRepository.delete(fid).toFuture();
    try {
      future.get(5, TimeUnit.SECONDS);
      log.info("<{}>应用标识[{}]清理完成", applicationName(), fid);
    } catch (InterruptedException e) {
      log.warn("Interrupted", e);
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      throw new IllegalStateException("<" + applicationName() + ">应用标识[" + fid + "]清理异常", e);
    }
  }

  // 初始化 Fid
  private int initFid() {
    var ran = new SecureRandom();
    var fidFuture = new CompletableFuture<Integer>();
    var isRun = new AtomicBoolean(true);
    var tempFid = new AtomicReference<Integer>();

    Supplier<Mono<Void>> makeFidFunc =
        () -> {
          tempFid.set(ran.nextInt(MAX_FID) + MIN_FID);

          // fid 是否存在
          var fidExists = new AtomicBoolean(false);
          var insertMono =
              Mono.defer(
                  () -> {
                    // 当 FID 不存在时执行插入操作
                    if (fidExists.get()) {
                      return Mono.empty();
                    }
                    var entity = newServerInstance(tempFid.get());
                    log.info("新增 ServerInstance: {}", entity);

                    return serverInstanceRepository
                        .insert(entity)
                        .doOnSuccess(
                            unused -> {
                              fidFuture.complete(tempFid.get());
                              isRun.set(false);
                            });
                  });

          return serverInstanceRepository
              .findById(tempFid.get())
              .filter(
                  si -> {
                    fidExists.set(true);
                    // 服务实例最后检查时间与当前时间相差超过1小时则视服务实例已销毁
                    var diff = Duration.between(si.getCheckTime(), LocalDateTime.now());
                    return diff.toSeconds() > INSTANCE_EXPIRES_IN;
                  })
              .flatMap(
                  si -> {
                    log.info("发现已过期的服务实例: {}", si);
                    var entity = newServerInstance(tempFid.get());
                    entity.setVer(si.getVer());
                    log.info("修改 ServerInstance: {}", entity);
                    return serverInstanceRepository.update(entity);
                  })
              .doOnSuccess(
                  unused -> {
                    fidFuture.complete(tempFid.get());
                    isRun.set(false);
                  })
              .switchIfEmpty(insertMono);
        };

    Mono.defer(makeFidFunc)
        .repeat(isRun::get)
        .onErrorContinue(
            ex -> {
              if (ex instanceof R2dbcDataIntegrityViolationException) {
                var t = (R2dbcDataIntegrityViolationException) ex;
                // 数据库唯一索引冲突时继续重试
                if ("23505".equals(t.getSqlState())) {
                  log.warn(
                      "初始化应用实例出现唯一索引冲突 fid:{}, [{}]:{}",
                      tempFid.get(),
                      t.getSqlState(),
                      t.getMessage());
                  return true;
                }
              }

              log.error("初始化应用实例异常 fid:{}", tempFid.get(), ex);
              return false;
            },
            (ex, o) -> {})
        .subscribe();

    try {
      return fidFuture.get(MAKE_FID_TIMEOUT, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.warn("Interrupted", e);
      Thread.currentThread().interrupt();
      throw new IllegalStateException("获取服务实例 ID 失败", e);
    } catch (ExecutionException | TimeoutException e) {
      throw new IllegalStateException(e);
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
    e.setAppName(applicationName());
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

  private void checkHealth() {
    serverInstanceRepository
        .updateCheckTime(fid)
        .doOnSuccess(unused -> log.debug("健康检查成功 fid: {}", fid))
        .onErrorContinue((ex, o) -> log.error("健康检查异常", ex))
        .delaySubscription(CHECK_HEALTH_PERIOD)
        .repeat(() -> true)
        .subscribe();
  }
}
