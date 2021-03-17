package top.yein.tethys.core.system.info;

import java.util.Set;
import javax.inject.Inject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.system.info.Info;
import top.yein.tethys.system.info.InfoContributor;
import top.yein.tethys.system.info.InfoService;

/** @author KK (kzou227@qq.com) */
public class InfoServiceImpl implements InfoService {

  private final Set<InfoContributor> infoContributors;

  /** @param contributors */
  @Inject
  public InfoServiceImpl(Set<InfoContributor> contributors) {
    this.infoContributors = contributors;
  }

  @Override
  public Mono<Info> info() {
    return Mono.defer(
        () -> {
          var builder = new Info.Builder();
          return Flux.fromIterable(infoContributors)
              .flatMap(infoContributor -> infoContributor.contribute(builder))
              .then(Mono.fromSupplier(() -> builder.build()));
        });
  }
}
