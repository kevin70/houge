package top.yein.tethys.core.system.info;

import com.google.common.collect.ImmutableMap;
import reactor.core.publisher.Mono;
import top.yein.tethys.system.info.Info.Builder;
import top.yein.tethys.system.info.InfoContributor;

/** @author KK (kzou227@qq.com) */
public class JavaInfoContributor implements InfoContributor {

  @Override
  public Mono<Void> contribute(Builder builder) {
    return Mono.defer(
        () -> {
          builder.withDetail(
              "java",
              ImmutableMap.of(
                  "version",
                  System.getProperty("java.version"),
                  "vendor",
                  System.getProperty("java.specification.vendor")));
          return Mono.empty();
        });
  }
}
