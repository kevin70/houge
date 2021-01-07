package top.yein.tethys.core.id;

import java.util.UUID;
import reactor.core.publisher.Flux;
import top.yein.tethys.id.MessageIdGenerator;

/**
 * 消息 ID 生成器 UUID 实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class UuidMessageIdGenerator implements MessageIdGenerator {

  @Override
  public Flux<String> nextIds() {
    return Flux.create(
        sink -> {
          long limit = sink.requestedFromDownstream();
          if (limit > REQUEST_IDS_LIMIT) {
            limit = REQUEST_IDS_LIMIT;
          }

          for (int i = 0; i < limit; i++) {
            sink.next(UUID.randomUUID().toString().replaceAll("-", ""));
          }
          sink.complete();
        });
  }
}
