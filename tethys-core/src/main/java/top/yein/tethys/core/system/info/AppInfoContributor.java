package top.yein.tethys.core.system.info;

import com.google.common.collect.ImmutableMap;
import javax.inject.Inject;
import reactor.core.publisher.Mono;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.system.info.Info.Builder;
import top.yein.tethys.system.info.InfoContributor;

/** @author KK (kzou227@qq.com) */
public class AppInfoContributor implements InfoContributor {

  private final ApplicationIdentifier applicationIdentifier;

  @Inject
  public AppInfoContributor(ApplicationIdentifier applicationIdentifier) {
    this.applicationIdentifier = applicationIdentifier;
  }

  @Override
  public Mono<Void> contribute(Builder builder) {
    return Mono.defer(
        () -> {
          builder.withDetail(
              "app",
              ImmutableMap.of(
                  "name",
                  applicationIdentifier.applicationName(),
                  "version",
                  applicationIdentifier.version(),
                  "fid",
                  applicationIdentifier.fid()));
          return Mono.empty();
        });
  }
}
