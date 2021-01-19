package top.yein.tethys.core.id;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import top.yein.tethys.id.MessageIdGenerator;

/**
 * {@link YeinGidMessageIdGenerator} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class YeinGidMessageIdGeneratorTest {

  @Test
  void nextIds() {
    var messageIdGenerator = new YeinGidMessageIdGenerator();
    var limitRequest = 9;
    var p1 = messageIdGenerator.nextIds().limitRequest(limitRequest);
    StepVerifier.create(p1).expectNextCount(limitRequest).verifyComplete();

    var p2 = messageIdGenerator.nextIds();
    StepVerifier.create(p2).expectNextCount(MessageIdGenerator.REQUEST_IDS_LIMIT).verifyComplete();
  }
}
