package top.yein.tethys.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import top.yein.tethys.common.JacksonUtils;

/**
 * @author KK (kzou227@qq.com)
 * @date 2020-12-30 15:10
 */
@Log4j2
public abstract class AbstractRestSupport {

  /** */
  public static final AttributeKey<QueryStringDecoder> QUERY_PARAMS_ATTRIBUTE_KEY =
      AttributeKey.newInstance("reactor.http.queryParams");

  private ObjectMapper objectMapper;

  //  @Inject
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * @param request
   * @param name
   * @return
   */
  protected String queryParam(HttpServerRequest request, String name) {
    var value = queryParams(request, name);
    if (value == null || value.isEmpty()) {
      return null;
    }
    return value.get(0);
  }

  /**
   * @param request
   * @param name
   * @return
   */
  protected List<String> queryParams(HttpServerRequest request, String name) {
    Map<String, List<String>> params = queryParams(request);
    var value = params.get(name);
    if (value == null || value.isEmpty()) {
      return null;
    }
    return value;
  }

  /**
   * @param request
   * @return
   */
  protected Map<String, List<String>> queryParams(HttpServerRequest request) {
    var connection = getConnection(request);
    var channel = connection.channel();
    if (channel.hasAttr(QUERY_PARAMS_ATTRIBUTE_KEY)) {
      return channel.attr(QUERY_PARAMS_ATTRIBUTE_KEY).get().parameters();
    }
    QueryStringDecoder query = new QueryStringDecoder(request.uri());
    channel.attr(QUERY_PARAMS_ATTRIBUTE_KEY).setIfAbsent(query);
    return query.parameters();
  }

  /**
   * @param request
   * @param clazz
   * @param <T>
   * @return
   */
  protected <T> Mono<T> json(HttpServerRequest request, Class<T> clazz) {
    return request
        .receiveContent()
        .map(
            httpContent -> {
              InputStream in = new ByteBufInputStream(httpContent.content());
              try {
                // FIXME 判断此处的 content-type
                return getObjectMapper().readValue(in, clazz);
              } catch (IOException e) {
                // FIXME 完善此处逻辑
                e.printStackTrace();
                throw new IllegalArgumentException(e);
              }
            })
        .next();
  }

  /**
   * @param response
   * @param value
   * @return
   */
  protected Mono<Void> json(HttpServerResponse response, Object value) {
    var buf = response.alloc().directBuffer();
    OutputStream out = new ByteBufOutputStream(buf);
    try {
      getObjectMapper().writeValue(out, value);
      return response
          .header(HttpHeaderNames.CONTENT_TYPE, MediaType.JSON_UTF_8.toString())
          .send(Mono.just(buf))
          .then();
    } catch (IOException e) {
      // 序列化 JSON 失败响应错误信息
      log.error("http response json 序列化错误 [value={}]", e, value);
      return response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR).send();
    }
  }

  /**
   * @param request
   * @return
   */
  private Connection getConnection(HttpServerRequest request) {
    Connection[] connections = new Connection[1];
    request.withConnection(connection -> connections[0] = connection);
    return connections[0];
  }

  private ObjectMapper getObjectMapper() {
    if (objectMapper == null) {
      return JacksonUtils.objectMapper();
    }
    return objectMapper;
  }
}
