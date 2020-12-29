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
package top.yein.tethys.session;

/**
 * Session 订阅取消订阅群组事件.
 *
 * @see SessionGroupListener
 * @see SessionGroupManager
 * @author KK (kzou227@qq.com)
 */
public enum SessionGroupEvent {
  /** 订阅成功之前. */
  GROUP_SUB_BEFORE,
  /** 订阅成功之后. */
  GROUP_SUB_AFTER,
  /** 取消订阅成功之前. */
  GROUP_UNSUB_BEFORE,
  /** 取消订阅成功之后. */
  GROUP_UNSUB_AFTER,
}
