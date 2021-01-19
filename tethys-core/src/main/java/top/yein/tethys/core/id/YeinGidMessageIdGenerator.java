package top.yein.tethys.core.id;

import reactor.core.publisher.Flux;
import top.yein.tethys.id.MessageIdGenerator;
import top.yein.tethys.util.YeinGid;

/**
 * YeinGid 消息 ID 生成器实现.
 *
 * @author KK (kzou227@qq.com)
 */
public class YeinGidMessageIdGenerator implements MessageIdGenerator {

  @Override
  public Flux<String> nextIds() {
    return Flux.create(
        sink -> {
          long limit = sink.requestedFromDownstream();
          if (limit > REQUEST_IDS_LIMIT) {
            limit = REQUEST_IDS_LIMIT;
          }

          var fid = 1;
          for (int i = 0; i < limit; i++) {
            sink.next(new YeinGid(fid).toHexString());
          }
          sink.complete();
        });
  }
}
