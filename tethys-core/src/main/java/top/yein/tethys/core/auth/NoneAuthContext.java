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
package top.yein.tethys.core.auth;

import top.yein.tethys.auth.AuthContext;

/**
 * 未认证的默认用户上下文.
 *
 * @author KK (kzou227@qq.com)
 */
public class NoneAuthContext implements AuthContext {

  /** 默认全局唯一的匿名访问实例. */
  public static final AuthContext INSTANCE = new NoneAuthContext();

  private NoneAuthContext() {
    if (INSTANCE != null) {
      throw new IllegalStateException("无法创建实例，使用 NonAuthContext.INSTANCE 静态变量默认实例");
    }
  }

  @Override
  public long uid() {
    return 0;
  }

  @Override
  public String token() {
    throw new IllegalStateException("匿名访问");
  }

  @Override
  public boolean isAnonymous() {
    return true;
  }
}
