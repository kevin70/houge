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
package top.yein.tethys.core.http;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

/**
 * 拦截器.
 *
 * @author KK (kzou227@qq.com)
 */
public class Interceptors {

  /** 认证拦截器. */
  private Function<
          BiFunction<
              ? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>>,
          BiFunction<
              ? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>>>
      authFunc;

  /**
   * 使用拦截回调函数创建实例.
   *
   * @param authFunc 认证拦截函数
   */
  public Interceptors(
      Function<
              BiFunction<
                  ? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>>,
              BiFunction<
                  ? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>>>
          authFunc) {
    Objects.requireNonNull(authFunc, "[authFunc]不能为null");
    this.authFunc = authFunc;
  }

  /**
   * 认证拦截器.
   *
   * @param next 执行成功后的下一个函数
   * @return
   */
  public BiFunction<
          ? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>>
      auth(
          BiFunction<
                  ? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>>
              next) {
    return authFunc.apply(next);
  }
}
