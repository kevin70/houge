package top.yein.tethys.util;

import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;
import java.util.List;
import java.util.Map;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;

/**
 * reactor-netty HTTP 服务端工具包.
 *
 * @author KK (kzou227@qq.com)
 */
public class ReactorHttpServerUtils {

  /** HTTP Query 参数解析存储的属性键. */
  public static final AttributeKey<QueryParameterHolder> QUERY_PARAMS_ATTRIBUTE_KEY =
      AttributeKey.newInstance("reactor.http.queryParams");

  private ReactorHttpServerUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 获取 {@link HttpServerRequest} 查询参数值.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @return 查询参数值
   */
  public static String queryParam(HttpServerRequest request, String name) {
    var value = queryParams(request, name);
    if (value == null || value.isEmpty()) {
      return null;
    }
    return value.get(0);
  }

  /**
   * 获取 {@link HttpServerRequest} 查询参数值列表.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @return 查询参数值列表
   */
  public static List<String> queryParams(HttpServerRequest request, String name) {
    Map<String, List<String>> params = queryParams(request);
    var value = params.get(name);
    if (value == null || value.isEmpty()) {
      return null;
    }
    return value;
  }

  /**
   * 获取 {@link HttpServerRequest} 查询所有查询参数.
   *
   * @param request HTTP 请求对象
   * @return 查询参数值映射
   */
  public static Map<String, List<String>> queryParams(HttpServerRequest request) {
    var connection = getConnection(request);
    var channel = connection.channel();
    if (channel.hasAttr(QUERY_PARAMS_ATTRIBUTE_KEY)) {
      var holder = channel.attr(QUERY_PARAMS_ATTRIBUTE_KEY).get();
      if (holder.connection == connection) {
        return holder.query.parameters();
      }
    }
    QueryStringDecoder query = new QueryStringDecoder(request.uri());
    channel.attr(QUERY_PARAMS_ATTRIBUTE_KEY).set(new QueryParameterHolder(connection, query));
    return query.parameters();
  }

  /**
   * 获取 {@link HttpServerRequest} 请求链接对象.
   *
   * @param request HTTP 请求对象
   * @return 链接对象
   */
  public static Connection getConnection(HttpServerRequest request) {
    Connection[] connections = new Connection[1];
    request.withConnection(connection -> connections[0] = connection);
    return connections[0];
  }

  static class QueryParameterHolder {
    Connection connection;
    QueryStringDecoder query;

    public QueryParameterHolder(Connection connection, QueryStringDecoder query) {
      this.connection = connection;
      this.query = query;
    }
  }
}
