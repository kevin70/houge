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
package top.yein.tethys.core.session;

import java.security.SecureRandom;
import top.yein.tethys.auth.AuthContext;

/**
 * 测试专用的 AuthContext.
 *
 * @author KK (kzou227@qq.com)
 */
public class TestAuthContext implements AuthContext {

  final long uid = new SecureRandom().nextLong();

  @Override
  public long uid() {
    return uid;
  }

  @Override
  public String token() {
    return Long.toHexString(uid);
  }

  @Override
  public boolean isAnonymous() {
    return false;
  }
}
