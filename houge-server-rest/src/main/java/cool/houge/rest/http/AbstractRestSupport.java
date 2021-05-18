/*
 * Copyright 2019-2021 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cool.houge.rest.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import cool.houge.auth.AuthContext;
import cool.houge.util.JsonUtils;
import cool.houge.util.ReactorHttpServerUtils;

/**
 * REST 抽象支撑类.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public abstract class AbstractRestSupport {

  /** 认证上下文存储的键值. */
  public static final Class<AuthContext> AUTH_CONTEXT_KEY = AuthContext.class;

  /**
   * 获取 {@link HttpServerRequest} 路径参数值.
   *
   * @param request HTTP 请求对象
   * @param name 路径参数名称
   * @return 路径参数值
   */
  protected String pathString(HttpServerRequest request, String name) {
    var value = request.param(name);
    if (Strings.isNullOrEmpty(value)) {
      throw new BizCodeException(BizCode.C912, Strings.lenientFormat("缺少必须的PATH参数[%s]", name));
    }
    return value;
  }

  /**
   * 获取 {@link HttpServerRequest} 路径参数值.
   *
   * @param request HTTP 请求对象
   * @param name 路径参数名称
   * @return 路径参数值
   */
  protected long pathLong(HttpServerRequest request, String name) {
    var value = pathString(request, name);
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new BizCodeException(
          BizCode.C910, Strings.lenientFormat("PATH参数[%s=%s]的值不是一个有效的Long值", name, value));
    }
  }

  /**
   * 获取 {@link HttpServerRequest} 路径参数值.
   *
   * @param request HTTP 请求对象
   * @param name 路径参数名称
   * @return 路径参数值
   */
  protected int pathInt(HttpServerRequest request, String name) {
    var value = pathString(request, name);
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new BizCodeException(
          BizCode.C910, Strings.lenientFormat("PATH参数[%s=%s]的值不是一个有效的Integer值", name, value));
    }
  }

  /**
   * 获取 {@link HttpServerRequest} 查询参数值.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @return 查询参数值
   */
  protected String queryParam(HttpServerRequest request, String name) {
    return ReactorHttpServerUtils.queryParam(request, name);
  }

  /**
   * 获取{@link HttpServerRequest}查询参数.
   *
   * <p>如果参数{@code name}值为{@code null}, 则返回{@code defaultValue}.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @param defaultValue 默认值
   * @return 查询参数值
   */
  protected String queryParam(HttpServerRequest request, String name, String defaultValue) {
    var value = queryParam(request, name);
    if (Strings.isNullOrEmpty(value)) {
      return defaultValue;
    }
    return value.trim();
  }

  /**
   * 获取{@link HttpServerRequest}查询参数.
   *
   * <p>如果参数{@code name}值为{@code null}时将会抛出{@link BizCode#C912}的业务异常.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @return 查询参数值
   */
  protected String requiredQueryParam(HttpServerRequest request, String name) {
    var value = queryParam(request, name);
    if (Strings.isNullOrEmpty(value)) {
      throw new BizCodeException(BizCode.C912, Strings.lenientFormat("缺少必须的QUERY参数[%s]", name));
    }
    return value.trim();
  }

  /**
   * 获取 {@link HttpServerRequest} 查询参数值列表.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @return 查询参数值列表
   */
  protected List<String> queryParams(HttpServerRequest request, String name) {
    return ReactorHttpServerUtils.queryParams(request, name);
  }

  /**
   * 获取 {@link HttpServerRequest} 查询所有查询参数.
   *
   * @param request HTTP 请求对象
   * @return 查询参数值映射
   */
  protected Map<String, List<String>> queryParams(HttpServerRequest request) {
    return ReactorHttpServerUtils.queryParams(request);
  }

  /**
   * 获取{@link HttpServerRequest}查询参数.
   *
   * <p>如果参数{@code name}值为{@code null}, 则返回{@code defaultValue}.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @param defaultValue 默认值
   * @return 查询参数值
   */
  protected int queryInt(HttpServerRequest request, String name, int defaultValue) {
    var value = queryParam(request, name);
    if (Strings.isNullOrEmpty(value)) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new BizCodeException(
          BizCode.C910, Strings.lenientFormat("QUERY参数[%s=%s]的值不是一个有效的Integer值", name, value));
    }
  }

  /**
   * 获取{@link HttpServerRequest}查询参数.
   *
   * <p>如果参数{@code name}值为{@code null}时将会抛出{@link BizCode#C912}的业务异常.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @return 查询参数值
   */
  protected int requiredQueryInt(HttpServerRequest request, String name) {
    var value = requiredQueryParam(request, name);
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new BizCodeException(
          BizCode.C910, Strings.lenientFormat("QUERY参数[%s=%s]的值不是一个有效的Integer值", name, value));
    }
  }

  /**
   * 获取{@link HttpServerRequest}查询参数.
   *
   * <p>如果参数{@code name}值为{@code null}, 则返回{@code defaultValue}.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @param defaultValue 默认值
   * @return 查询参数值
   */
  protected long queryLong(HttpServerRequest request, String name, long defaultValue) {
    var value = queryParam(request, name);
    if (Strings.isNullOrEmpty(value)) {
      return defaultValue;
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new BizCodeException(
          BizCode.C910, Strings.lenientFormat("QUERY参数[%s=%s]的值不是一个有效的Long值", name, value));
    }
  }

  /**
   * 获取{@link HttpServerRequest}查询参数.
   *
   * <p>如果参数{@code name}值为{@code null}时将会抛出{@link BizCode#C912}的业务异常.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @return 查询参数值
   */
  protected long requiredQueryLong(HttpServerRequest request, String name) {
    var value = requiredQueryParam(request, name);
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new BizCodeException(
          BizCode.C910, Strings.lenientFormat("QUERY参数[%s=%s]的值不是一个有效的Long值", name, value));
    }
  }

  /**
   * 获取{@link HttpServerRequest}查询参数.
   *
   * @param request HTTP 请求对象
   * @param name 查询参数名称
   * @param dvSupplier 默认值回调
   * @return 查询参数值
   */
  protected LocalDateTime queryDateTime(
      HttpServerRequest request, String name, Supplier<LocalDateTime> dvSupplier) {
    var value = queryParam(request, name);
    if (Strings.isNullOrEmpty(value)) {
      return dvSupplier.get();
    }
    try {
      return LocalDateTime.parse(value);
    } catch (DateTimeParseException e) {
      throw new BizCodeException(
          BizCode.C910,
          Strings.lenientFormat("QUERY参数[%s=%s]的值格式应该像'2011-12-03T10:15:30'", name, value));
    }
  }

  /**
   * 解析 HTTP 请求 JSON BODY.
   *
   * @param request HTTP 请求对象
   * @param clazz body class
   * @param <T> 泛型
   * @return RS
   */
  protected <T> Mono<T> json(HttpServerRequest request, Class<T> clazz) {
    var contentType = request.requestHeaders().get(HttpHeaderNames.CONTENT_TYPE);
    try {
      if (!MediaType.JSON_UTF_8.is(MediaType.parse(contentType))) {
        throw new BizCodeException(BizCode.C406, "不支持的 content-type=" + contentType);
      }
    } catch (IllegalArgumentException e) {
      throw new BizCodeException(BizCode.C406, "错误的 content-type=" + contentType, e);
    }

    return request
        .receiveContent()
        .map(
            httpContent -> {
              InputStream in = new ByteBufInputStream(httpContent.content());
              try {
                return getObjectMapper().readValue(in, clazz);
              } catch (IOException e) {
                throw new BizCodeException(BizCode.C400, "解析JSON异常", e);
              }
            })
        .next();
  }

  /**
   * 输入 HTTP 响应 JSON BODY.
   *
   * @param response HTTP 响应对象
   * @param value 响应 BODY 对象
   * @return RS
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
   * 返回 {@link reactor.util.context.Context} 中存储的认证上下文信息.
   *
   * <p>如果未找到认证上下文将返回 {@link BizCode#C401} 业务异常.
   *
   * @return 认证上下文
   * @see BizCodeException
   */
  protected Mono<AuthContext> authContext() {
    return Mono.deferContextual(
        context -> {
          if (!context.hasKey(AUTH_CONTEXT_KEY)) {
            return Mono.error(new BizCodeException(BizCode.C401, "未找到 AuthContext"));
          }
          return Mono.just(context.get(AUTH_CONTEXT_KEY));
        });
  }

  private ObjectMapper getObjectMapper() {
    return JsonUtils.objectMapper();
  }
}
