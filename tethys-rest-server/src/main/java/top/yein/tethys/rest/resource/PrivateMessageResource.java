package top.yein.tethys.rest.resource;

import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.query.PrivateMessageQuery;
import top.yein.tethys.service.PrivateMessageService;
import top.yein.tethys.vo.BatchReadMessageVO;

/**
 * 私聊消息 REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
@Component
public class PrivateMessageResource extends AbstractRestSupport implements RoutingService {

  private final PrivateMessageService privateMessageService;

  /**
   * 构造函数.
   *
   * @param privateMessageService 私聊消息服务
   */
  public PrivateMessageResource(PrivateMessageService privateMessageService) {
    this.privateMessageService = privateMessageService;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.get("/private-messages/recent", interceptors.auth(this::findRecentMessages));
    routes.put("/private-messages/read-status/batch", interceptors.auth(this::batchReadMessage));
  }

  /**
   * 查询用户私聊消息.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  public Mono<Void> findRecentMessages(HttpServerRequest request, HttpServerResponse response) {
    return authContext()
        .flatMap(
            ac -> {
              var createTime =
                  queryDateTime(request, "create_time", () -> LocalDateTime.now().minusDays(3));
              // 查询对象
              var query = new PrivateMessageQuery();
              query.setReceiverId(ac.uid());
              query.setCreateTime(createTime);
              query.setLimit(queryInt(request, "limit", 500));
              query.setOffset(queryInt(request, "offset", 0));

              return privateMessageService
                  .findRecentMessages(query)
                  .collectList()
                  .flatMap(privateMessages -> json(response, privateMessages));
            });
  }

  /**
   * 将消息设置为已读状态.
   *
   * @return RS
   */
  public Mono<Void> readMessage(HttpServerRequest request, HttpServerResponse response) {
    //    json(request)
    return Mono.empty();
  }

  /**
   * 批量将消息设置为已读状态.
   *
   * @param request
   * @param response
   * @return
   */
  public Mono<Void> batchReadMessage(HttpServerRequest request, HttpServerResponse response) {
    return authContext()
        .flatMap(
            ac ->
                json(request, BatchReadMessageVO.class)
                    .flatMap(vo -> privateMessageService.batchReadMessage(vo, ac.uid())))
        .then(response.status(HttpResponseStatus.NO_CONTENT).send());
  }
}
