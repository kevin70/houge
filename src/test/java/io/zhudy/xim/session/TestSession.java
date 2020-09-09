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
package io.zhudy.xim.session;

import java.security.SecureRandom;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 测试专用的 Session.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class TestSession implements Session {

  private final long sessionId = new SecureRandom().nextLong();
  private final Set<String> subGroupIds = new LinkedHashSet<>();

  @Override
  public String sessionId() {
    return String.valueOf(sessionId);
  }

  @Override
  public String uid() {
    if (isAnonymous()) {
      return Long.toHexString(sessionId);
    }
    return authContext().uid();
  }

  @Override
  public Set<String> subGroupIds() {
    return subGroupIds;
  }
}
