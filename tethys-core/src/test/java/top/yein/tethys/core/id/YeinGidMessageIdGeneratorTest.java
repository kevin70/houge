package top.yein.tethys.core.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.id.MessageIdGenerator;

/**
 * {@link YeinGidMessageIdGenerator} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class YeinGidMessageIdGeneratorTest {

  @Test
  void nextId() {
    var applicationIdentifier = mock(ApplicationIdentifier.class);
    when(applicationIdentifier.fid()).thenReturn(0);

    var messageIdGenerator = new YeinGidMessageIdGenerator(applicationIdentifier);
    assertThat(messageIdGenerator.nextId()).isNotBlank();
  }

  @Test
  void nextIds() {
    var applicationIdentifier = mock(ApplicationIdentifier.class);
    when(applicationIdentifier.fid()).thenReturn(0);

    var messageIdGenerator = new YeinGidMessageIdGenerator(applicationIdentifier);
    var limitRequest = 9;
    var p1 = messageIdGenerator.nextIds().limitRequest(limitRequest);
    StepVerifier.create(p1).expectNextCount(limitRequest).verifyComplete();

    var p2 = messageIdGenerator.nextIds();
    StepVerifier.create(p2).expectNextCount(MessageIdGenerator.REQUEST_IDS_LIMIT).verifyComplete();
  }
}
