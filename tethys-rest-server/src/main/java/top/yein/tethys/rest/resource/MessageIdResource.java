package top.yein.tethys.rest.resource;

import java.util.Optional;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.HttpServerRoutes;
import top.yein.chaos.biz.BizCodeException;
import top.yein.tethys.core.BizCodes;
import top.yein.tethys.core.http.AbstractRestSupport;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.id.MessageIdGenerator;

/**
 * 消息 ID REST 接口.
 *
 * @author KK (kzou227@qq.com)
 */
@Component
public class MessageIdResource extends AbstractRestSupport implements RoutingService {

  private final MessageIdGenerator messageIdGenerator;

  /**
   * 构造函数.
   *
   * @param messageIdGenerator 消息 ID 生成器
   */
  public MessageIdResource(MessageIdGenerator messageIdGenerator) {
    this.messageIdGenerator = messageIdGenerator;
  }

  @Override
  public void update(HttpServerRoutes routes, Interceptors interceptors) {
    routes.get("/message-ids", interceptors.auth(this::getMessageIds));
  }

  /**
   * 获取消息 ID 列表.
   *
   * @param request 请求对象
   * @param response 响应对象
   * @return RS
   */
  public Mono<Void> getMessageIds(HttpServerRequest request, HttpServerResponse response) {
    var limit =
        Optional.ofNullable(queryParam(request, "limit"))
            .map(
                v -> {
                  try {
                    return Integer.parseInt(v);
                  } catch (NumberFormatException e) {
                    throw new BizCodeException(BizCodes.C910);
                  }
                })
            .orElse(MessageIdGenerator.REQUEST_IDS_LIMIT);
    return messageIdGenerator
        .nextIds()
        .limitRequest(limit)
        .collectList()
        .flatMap(ids -> json(response, ids));
  }
}
