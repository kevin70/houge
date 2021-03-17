package top.yein.tethys.core.system.health;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.system.health.Health;
import top.yein.tethys.system.health.HealthComposite;
import top.yein.tethys.system.health.HealthIndicator;
import top.yein.tethys.system.health.HealthService;
import top.yein.tethys.system.health.HealthStatus;

/**
 * 运行健康状况服务实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class HealthServiceImpl implements HealthService {

  /** 运行健康状况状态排序. */
  private static final List<HealthStatus> STATUS_ORDER =
      List.of(
          HealthStatus.DOWN, HealthStatus.OUT_OF_SERVICE, HealthStatus.UP, HealthStatus.UNKNOWN);

  private static final Comparator<Health> HEALTH_COMPARATOR =
      (o1, o2) -> {
        var i1 = STATUS_ORDER.indexOf(o1.getStatus());
        var i2 = STATUS_ORDER.indexOf(o2.getStatus());
        return (i1 < i2) ? -1 : (i1 != i2) ? 1 : o1.getStatus().compareTo(o2.getStatus());
      };

  private final Set<HealthIndicator> healthIndicators;

  /**
   * 使用 {@link HealthIndicator} 创建对象.
   *
   * @param healthIndicators 运行健康状况指示器实现
   */
  @Inject
  public HealthServiceImpl(Set<HealthIndicator> healthIndicators) {
    this.healthIndicators = healthIndicators;
  }

  @Override
  public Mono<HealthComposite> health(boolean includeDetails) {
    return Flux.fromIterable(healthIndicators)
        .flatMap(healthIndicator -> healthIndicator.getHealth(includeDetails))
        .sort(HEALTH_COMPARATOR)
        .collectList()
        .map(
            list -> {
              if (list.isEmpty()) {
                return HealthComposite.builder().status(HealthStatus.UP).build();
              }
              var f = list.get(0);
              return HealthComposite.builder().status(f.getStatus()).components(list).build();
            });
  }
}
