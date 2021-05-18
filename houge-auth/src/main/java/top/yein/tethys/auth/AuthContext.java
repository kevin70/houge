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
package top.yein.tethys.auth;

/**
 * 认证的上下文信息.
 *
 * @author KK (kzou227@qq.com)
 */
public interface AuthContext {

  /**
   * 返回用户 ID.
   *
   * <p>匿名访问用户 ID 为 0.
   *
   * @return 用户 ID
   */
  long uid();

  /**
   * 返回认证令牌.
   *
   * @return 认证令牌
   */
  String token();
}
