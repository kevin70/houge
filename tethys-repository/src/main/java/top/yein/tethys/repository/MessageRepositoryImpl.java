package top.yein.tethys.repository;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.yein.tethys.entity.Message;

/** @author KK (kzou227@qq.com) */
public class MessageRepositoryImpl implements MessageRepository {

  @Override
  public Mono<Integer> insert(Message entity) {
    return null;
  }

  @Override
  public Mono<Integer> updateUnread(String id, int v) {
    return null;
  }

  @Override
  public Mono<Message> findById(String id) {
    return null;
  }

  @Override
  public Flux<Message> findByIds(List<String> ids) {
    return null;
  }
}
