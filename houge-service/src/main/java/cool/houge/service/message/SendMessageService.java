package cool.houge.service.message;

import reactor.core.publisher.Mono;

/** @author KK (kzou227@qq.com) */
public interface SendMessageService {

  /**
   * @param input
   * @return
   */
  Mono<Void> sendToUser(SendMessageInput input);

  /**
   * @param input
   * @return
   */
  Mono<Void> sendToGroup(SendMessageInput input);
}
