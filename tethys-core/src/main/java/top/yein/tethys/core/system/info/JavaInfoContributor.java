package top.yein.tethys.core.system.info;

import reactor.core.publisher.Mono;
import top.yein.tethys.system.info.Info.Builder;
import top.yein.tethys.system.info.InfoContributor;

/** @author KK (kzou227@qq.com) */
public class JavaInfoContributor implements InfoContributor {

  @Override
  public Mono<Void> contribute(Builder builder) {
    return null;
  }
}
