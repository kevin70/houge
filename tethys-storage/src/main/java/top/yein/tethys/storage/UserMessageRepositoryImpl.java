package top.yein.tethys.storage;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.UserMessage;

/** @author KK (kzou227@qq.com) */
public class UserMessageRepositoryImpl implements UserMessageRepository {

  @Override
  public Mono<Integer> insert(UserMessage entity) {
    return null;
  }

  @Override
  public Mono<Integer> batchInsert(List<UserMessage> entities) {
    return null;
  }

  @Override
  public Flux<Void> findDetailsByUid(long uid) {
    return null;
  }
}
