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

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
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
  private final UnaryOperator<BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>>>
      authFunc;
  /** 服务认证拦截器. */
  private final UnaryOperator<BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>>>
      serviceAuthFunc;

  /**
   * 使用拦截回调函数创建实例.
   *
   * @param authFunc 认证拦截函数
   * @param serviceAuthFunc 服务认证拦截器
   */
  public Interceptors(
      UnaryOperator<BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>>> authFunc,
      UnaryOperator<BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>>>
          serviceAuthFunc) {
    Objects.requireNonNull(authFunc, "[authFunc]不能为null");
    Objects.requireNonNull(serviceAuthFunc, "[serviceAuthFunc]不能为null");
    this.authFunc = authFunc;
    this.serviceAuthFunc = serviceAuthFunc;
  }

  /**
   * 认证拦截器.
   *
   * @param next 执行成功后的下一个函数
   * @return 认证拦截器
   */
  public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> userAuth(
      BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> next) {
    return authFunc.apply(next);
  }

  /**
   * 返回服务认证拦截器.
   *
   * @param next 执行成功后的下一个函数
   * @return 服务认证拦截器
   */
  public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> serverAuth(
      BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> next) {
    return serviceAuthFunc.apply(next);
  }
}
