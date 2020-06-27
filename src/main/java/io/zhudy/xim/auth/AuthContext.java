/*
 * Copyright 2019-2020 the original author or authors
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
package io.zhudy.xim.auth;

import javax.annotation.Nonnull;

/**
 * 认证的上下文信息.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface AuthContext {

  /** 未认证或匿名访问的上下文信息. */
  AuthContext NONE_AUTH_CONTEXT =
      new AuthContext() {

        @Override
        public String uid() {
          throw new IllegalStateException("匿名访问");
        }

        @Override
        public String token() {
          throw new IllegalStateException("匿名访问");
        }
      };

  /**
   * 返回用户 ID.
   *
   * @return 用户 ID
   */
  @Nonnull
  String uid();

  /**
   * 返回认证令牌.
   *
   * @return 认证令牌
   */
  @Nonnull
  String token();

  /**
   * 是否为匿名认证.
   *
   * @return true 是匿名认证
   */
  default boolean isAnonymous() {
    return true;
  }
}
