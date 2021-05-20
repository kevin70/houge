package cool.houge.system.info;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cool.houge.system.identifier.ApplicationIdentifier;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

/**
 * {@link AppInfoContributor} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class AppInfoContributorTest {

  @Test
  void contribute() {
    var appName = UUID.randomUUID().toString();
    var version = UUID.randomUUID().toString();
    var fid = new SecureRandom().nextInt();

    var identifier = mock(ApplicationIdentifier.class);
    when(identifier.applicationName()).thenReturn(appName);
    when(identifier.version()).thenReturn(version);
    when(identifier.fid()).thenReturn(fid);

    var contributor = new AppInfoContributor(identifier);
    var builder = new Info.Builder();
    StepVerifier.create(contributor.contribute(builder)).expectComplete().verify();

    var details = builder.build().getDetails();
    Map<String, Object> appInfo = (Map<String, Object>) details.get("app");

    assertThat(appInfo)
        .containsEntry("name", appName)
        .containsEntry("version", version)
        .containsEntry("fid", fid)
        .containsKey("start_time");
  }
}
