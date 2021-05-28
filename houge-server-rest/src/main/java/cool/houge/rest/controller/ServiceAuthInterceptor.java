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
package cool.houge.rest.controller;

import io.netty.handler.codec.http.HttpHeaderNames;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import top.yein.chaos.biz.BizCode;
import top.yein.chaos.biz.BizCodeException;
import cool.houge.rest.http.AbstractRestSupport;

/**
 * 服务认证拦截器.
 *
 * @author KK (kzou227@qq.com)
 */
@Log4j2
public class ServiceAuthInterceptor extends AbstractRestSupport {

  private static final String AUTH_BASIC_SCHEME = "Basic";

  private final Map<String, String> basicUsers;

  /**
   * 使用 BASIC 认证用户构造对象.
   *
   * @param basicUsers BASIC 认证用户信息及密码
   */
  public ServiceAuthInterceptor(Map<String, String> basicUsers) {
    Objects.requireNonNull(basicUsers, "[basicUsers]不能为空");
    this.basicUsers = basicUsers;
  }

  /**
   * 服务认证处理.
   *
   * @param next 认证成功执行的回调
   * @return 认证处理
   */
  public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> handle(
      BiFunction<? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>>
          next) {
    return (request, response) -> {
      var authorization = request.requestHeaders().get(HttpHeaderNames.AUTHORIZATION);
      if (authorization == null || authorization.isEmpty()) {
        throw new BizCodeException(BizCode.C401, "缺少 authorization");
      }
      var beginIndex = AUTH_BASIC_SCHEME.length() + 1;
      if (authorization.length() <= beginIndex) {
        throw new BizCodeException(BizCode.C401, "不符合规范的 basic authorization");
      }

      // 解码
      byte[] bytes;
      try {
        bytes = Base64.getDecoder().decode(authorization.substring(beginIndex));
      } catch (IllegalArgumentException e) {
        log.debug("服务认证 BASE64 解码失败 [authorization={}]", authorization);
        throw new BizCodeException(BizCode.C401, "非法的 authorization");
      }
      var pass = new String(bytes, StandardCharsets.UTF_8).split(":");
      if (pass.length != 2) {
        throw new BizCodeException(BizCode.C401, "非法的 authorization");
      }
      var password = basicUsers.get(pass[0]);
      if (password == null) {
        log.debug("服务认证用户不存在 [user={}]", pass[0]);
        throw new BizCodeException(BizCode.C401, "认证失败")
            .addContextValue("user", pass[0]);
      }
      if (!password.equals(pass[1])) {
        log.debug("服务认证密码不匹配 [user={}]", pass[0]);
        throw new BizCodeException(BizCode.C401, "认证失败")
            .addContextValue("user", pass[0]);
      }
      return next.apply(request, response);
    };
  }
}
