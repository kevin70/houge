package top.yein.tethys.core.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;

/**
 * MOCK.
 *
 * @author KK (kzou227@qq.com)
 */
public class MockHttpServerRequest implements HttpServerRequest {

  private final Connection connection;
  private final ByteBuf content;
  private final Map<String, String> pathParams;
  private final InetSocketAddress hostAddress;
  private final InetSocketAddress remoteAddress;
  private final HttpHeaders requestHeaders;
  private final String scheme;
  private final Map<CharSequence, Set<Cookie>> cookies;
  private final boolean keepAlive;
  private final HttpMethod method;
  private final String fullPath;
  private final String uri;
  private final HttpVersion version;

  private MockHttpServerRequest(
      Connection connection,
      ByteBuf content,
      Map<String, String> pathParams,
      InetSocketAddress hostAddress,
      InetSocketAddress remoteAddress,
      HttpHeaders requestHeaders,
      String scheme,
      Map<CharSequence, Set<Cookie>> cookies,
      boolean keepAlive,
      HttpMethod method,
      String fullPath,
      String uri,
      HttpVersion version) {
    this.connection = connection;
    this.content = content;
    this.pathParams = pathParams;
    this.hostAddress = hostAddress;
    this.remoteAddress = remoteAddress;
    this.requestHeaders = requestHeaders;
    this.scheme = scheme;
    this.cookies = cookies;
    this.keepAlive = keepAlive;
    this.method = method;
    this.fullPath = fullPath;
    this.uri = uri;
    this.version = version;
  }

  @Override
  public ByteBufFlux receive() {
    return ByteBufFlux.fromInbound(Mono.just(content));
  }

  @Override
  public Flux<?> receiveObject() {
    return Flux.just(new DefaultHttpContent(content));
  }

  @Override
  public MockHttpServerRequest withConnection(Consumer<? super Connection> withConnection) {
    Objects.requireNonNull(withConnection, "withConnection");
    withConnection.accept(connection);
    return this;
  }

  @Override
  public String param(CharSequence key) {
    return pathParams.get(key);
  }

  @Override
  public Map<String, String> params() {
    return pathParams;
  }

  @Override
  public HttpServerRequest paramsResolver(
      Function<? super String, Map<String, String>> paramsResolver) {
    return this;
  }

  @Override
  public InetSocketAddress hostAddress() {
    return hostAddress;
  }

  @Override
  public InetSocketAddress remoteAddress() {
    return remoteAddress;
  }

  @Override
  public HttpHeaders requestHeaders() {
    return requestHeaders;
  }

  @Override
  public String scheme() {
    return scheme;
  }

  @Override
  public Map<CharSequence, Set<Cookie>> cookies() {
    return cookies;
  }

  @Override
  public boolean isKeepAlive() {
    return keepAlive;
  }

  @Override
  public boolean isWebsocket() {
    return false;
  }

  @Override
  public HttpMethod method() {
    return method;
  }

  @Override
  public String fullPath() {
    return fullPath;
  }

  @Override
  public String uri() {
    return uri;
  }

  @Override
  public HttpVersion version() {
    return version;
  }

  /**
   * 返回构建器实例.
   *
   * @return 构建器
   */
  public static Builder builder() {
    return new Builder();
  }

  /** 构建器. */
  public static class Builder {

    private Connection connection = Connection.from(new EmbeddedChannel());
    private ByteBuf content;
    private Map<String, String> pathParams = new LinkedHashMap<>();
    private InetSocketAddress hostAddress;
    private InetSocketAddress remoteAddress;
    private HttpHeaders requestHeaders;
    private String scheme;
    private Map<CharSequence, Set<Cookie>> cookies = new LinkedHashMap<>();
    private boolean keepAlive;
    private HttpMethod method;
    private String fullPath;
    private String uri;
    private HttpVersion version;

    private Builder() {}

    public Builder connection(Connection connection) {
      this.connection = connection;
      return this;
    }

    public Builder content(ByteBuf content) {
      this.content = content;
      return this;
    }

    public Builder pathParams(Map<String, String> pathParams) {
      this.pathParams = pathParams;
      return this;
    }

    public Builder hostAddress(InetSocketAddress hostAddress) {
      this.hostAddress = hostAddress;
      return this;
    }

    public Builder remoteAddress(InetSocketAddress remoteAddress) {
      this.remoteAddress = remoteAddress;
      return this;
    }

    public Builder requestHeaders(HttpHeaders requestHeaders) {
      this.requestHeaders = requestHeaders;
      return this;
    }

    public Builder scheme(String scheme) {
      this.scheme = scheme;
      return this;
    }

    public Builder cookies(Map<CharSequence, Set<Cookie>> cookies) {
      this.cookies = cookies;
      return this;
    }

    public Builder keepAlive(boolean keepAlive) {
      this.keepAlive = keepAlive;
      return this;
    }

    public Builder method(HttpMethod method) {
      this.method = method;
      return this;
    }

    public Builder fullPath(String fullPath) {
      this.fullPath = fullPath;
      return this;
    }

    public Builder uri(String uri) {
      this.uri = uri;
      return this;
    }

    public Builder version(HttpVersion version) {
      this.version = version;
      return this;
    }

    /**
     * 返回构建的 {@link MockHttpServerRequest} 实例.
     *
     * @return {@link MockHttpServerRequest}
     */
    public MockHttpServerRequest build() {
      return new MockHttpServerRequest(
          connection,
          content,
          pathParams,
          hostAddress,
          remoteAddress,
          requestHeaders,
          scheme,
          cookies,
          keepAlive,
          method,
          fullPath,
          uri,
          version);
    }
  }
}
