package cool.houge.system.info;

import java.util.Set;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

/**
 * {@link InfoServiceImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class InfoServiceImplTest {

  @Test
  void info() {
    var infoService = new InfoServiceImpl(Set.of());
    StepVerifier.create(infoService.info())
        .assertNext(info -> info.getDetails().isEmpty())
        .expectComplete()
        .verify();
  }
}
